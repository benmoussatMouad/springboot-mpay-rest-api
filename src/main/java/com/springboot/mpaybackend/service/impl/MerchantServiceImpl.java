package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.service.BmService;
import com.springboot.mpaybackend.service.LicenseService;
import com.springboot.mpaybackend.service.MerchantService;
import com.springboot.mpaybackend.service.TmService;
import com.springboot.mpaybackend.utils.RibProcessor;
import jakarta.transaction.Transactional;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantServiceImpl implements MerchantService {

    MerchantRepository merchantRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    ModelMapper modelMapper;
    WilayaRepository wilayaRepository;
    MerchantAccountRepository merchantAccountRepository;
    BankRepository bankRepository;
    MerchantStatusTraceRepository merchantStatusTraceRepository;
    MerchantLicenseRepository merchantLicenseRepository;
    MerchantAccountBlockTraceRepository merchantAccountBlockTraceRepository;
    AgencyRepository agencyRepository;
    BmService bmService;
    TmService tmService;
    BmRepository bmRepository;
    TmRepository tmRepository;
    UserBankRepository userBankRepository;
    UserAgencyRepository userAgencyRepository;
    LicenseService licenseService;

    public MerchantServiceImpl(TmRepository tmRepository, MerchantRepository merchantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, WilayaRepository wilayaRepository, MerchantAccountRepository merchantAccountRepository, BankRepository bankRepository, MerchantStatusTraceRepository merchantStatusTraceRepository, MerchantLicenseRepository merchantLicenseRepository, MerchantAccountBlockTraceRepository merchantAccountBlockTraceRepository, AgencyRepository agencyRepository, BmService bmService, TmService tmService,BmRepository bmRepository, UserBankRepository userBankRepository, UserAgencyRepository userAgencyRepository, LicenseService licenseService) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.wilayaRepository = wilayaRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.bankRepository = bankRepository;
        this.merchantStatusTraceRepository = merchantStatusTraceRepository;
        this.merchantLicenseRepository = merchantLicenseRepository;
        this.merchantAccountBlockTraceRepository = merchantAccountBlockTraceRepository;
        this.agencyRepository = agencyRepository;
        this.bmService = bmService;
        this.tmService = tmService;
        this.bmRepository = bmRepository;
        this.tmRepository = tmRepository;
        this.userBankRepository = userBankRepository;
	this.userAgencyRepository = userAgencyRepository;
        this.agencyRepository = agencyRepository;
        this.licenseService = licenseService;


        this.modelMapper.getConfiguration().setSkipNullEnabled(true);

        // Mapping rule
        Converter<String, User> usernameConverter = context -> {
            String username = context.getSource();
            User user = new User();
            user.setUsername(username);
            return user;
        };
        Converter<Long, Wilaya> wilayaConverter = context -> {
            Long wilayaId = context.getSource();
            return wilayaRepository.findById( wilayaId )
                    .orElseThrow( () -> new ResourceNotFoundException( "Wilaya", "id", wilayaId ) );
        };

        this.modelMapper.addConverter( usernameConverter );
        this.modelMapper.addConverter( wilayaConverter );

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantResponseDto addMerchant(MerchantDto dto, Boolean byBankUser, String username) {

        // check if Merchant exists by phone
        if(merchantRepository.existsByPhone( dto.getPhone() ) ) {
            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "Phone already exists.");
        }

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Username already exists.");
        }

        // 1- Creating Merchant, User object is created by model Mapper
        Merchant merchant = modelMapper.map( dto, Merchant.class );
        if( byBankUser ) { // If the merchant is created by bank user, make status to In Progress + Create an account directly

            User callingUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Calling user", "username", username));

            if (callingUser.getUserType().equals(UserType.AGENCY_USER)
                    || callingUser.getUserType().equals(UserType.AGENCY_ADMIN)) {

                UserAgency userAgency = userAgencyRepository.findByUsernameUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("Calling user", "username", username));

                merchant.setBank(userAgency.getAgency().getBank());
                merchant.setAgency(userAgency.getAgency());
            }
            if (callingUser.getUserType().equals(UserType.BANK_USER)
                    || callingUser.getUserType().equals(UserType.BANK_ADMIN)) {

                UserBank userBank = userBankRepository.findByUsernameUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("Calling user", "username", username));

                merchant.setBank(userBank.getBank());
            }

            merchant.setStatus( MerchantStatus.IN_PROGRESS );
        }

        // 2- Setting up User
        User user = merchant.getUsername();
        user.setUsername( dto.getUsername() );
        user.setUserType( UserType.MERCHANT );
        user.setPhone( dto.getPhone() );
        user.setPassword( passwordEncoder.encode( dto.getPassword() ) );

        //TODO: Move it elsewhere
        // 3 Creating the trace
        if (!byBankUser) {
            MerchantStatusTrace trace = new MerchantStatusTrace();
            trace.setMerchant(merchant);
            trace.setCreatedAt(new Date());
            trace.setUser(user);
            trace.setStatus(MerchantStatus.NON_VERIFIED);

            merchantStatusTraceRepository.save(trace);
        }

        Merchant savedMerchant = merchantRepository.save( merchant );

        return modelMapper.map( savedMerchant, MerchantResponseDto.class );
    }

    @Override
    public MerchantResponseDto getMerchant(Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        MerchantResponseDto dto = modelMapper.map( merchant, MerchantResponseDto.class );
        List<MerchantLicense> licenses = merchantLicenseRepository.findByMerchantId( id );
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElse( null );

        // FIXME
        if( licenses.size() != 0 ) {
            dto.setTerminalId( licenses.get( 0 ).getTerminalId() );
        }
        if( account != null ) {
            dto.setAccountStatus( account.isAccountStatus() );
            dto.setRib( account.getAccountNumber() );
        }
        return dto;
    }

    @Override
    public MerchantResponseDto getMerchantByUsername(String username) {
        Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse( username )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "username", username ) );
        MerchantResponseDto dto = modelMapper.map( merchant, MerchantResponseDto.class );
        List<MerchantLicense> licenses = merchantLicenseRepository.findByMerchantId( merchant.getId() );
        MerchantAccount account = merchantAccountRepository.findByMerchantId( merchant.getId() )
                .orElse( null );

        if( licenses.size() != 0 ) {
            dto.setTerminalId( licenses.get( 0 ).getTerminalId() );
        }
        if( account != null ) {
            dto.setAccountStatus( account.isAccountStatus() );
        }

        return dto;
    }

    @Override
    public List<MerchantResponseDto> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAllByDeletedFalse();

        return merchants.stream().map( (element -> modelMapper.map( element, MerchantResponseDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public List<MerchantResponseDto> getAllMerchants(int page, int size) {
        List<Merchant> merchants = merchantRepository.findAllByDeletedFalse( PageRequest.of( page, size ) );

        return merchants.stream().map( (element -> modelMapper.map( element, MerchantResponseDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public MerchantPageDto getAllMerchantsByFilter(int page, int size, Long id, String firstName, String lastName, String phone, String registreCommerce, String numeroFiscal, String status) {

        Page<Merchant> merchants = merchantRepository.findByFilter( PageRequest.of( page, size ), id, firstName, lastName, phone, registreCommerce, numeroFiscal, (status!=null ? MerchantStatus.valueOf( status ) : null) );

        return makePageDto( merchants );
    }

    // @Utility
    private MerchantPageDto makePageDto(Page<Merchant> merchants) {
        List<MerchantResponseDto> merchantResponseDtos = merchants.stream().map( e -> modelMapper.map( e, MerchantResponseDto.class ) ).collect( Collectors.toList() );

        MerchantPageDto pageDto = new MerchantPageDto();
        pageDto.setPage( merchantResponseDtos );
        pageDto.setCount( merchants.getTotalElements() );
        return pageDto;
    }

    @Override
    public MerchantResponseDto updateMerchant(MerchantDto dto, Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        dto.setUsername( null );
        dto.setPassword( null );

        Merchant updatedMerchant = modelMapper.map( dto, Merchant.class );
        updatedMerchant.setId( null );

        //FIXME:
        System.out.println(updatedMerchant);

        modelMapper.map(updatedMerchant, merchant );

        merchant = merchantRepository.save( merchant );

        return modelMapper.map( merchant, MerchantResponseDto.class );
    }

    @Override
    public void deleteMerchant(Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );
        merchant.setDeleted( true );
        merchantRepository.save( merchant );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantResponseDto fillInfo(MerchantBankInfoDto dto, Long id) {


        // Find merchant, check if his status is non verified i.e initial status
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        if( !merchant.getStatus().equals( MerchantStatus.NON_VERIFIED ) ) {
            throw new MPayAPIException( "Merchant status should be in initial state NON_VERIFIED, action not authorized", HttpStatus.FORBIDDEN, "Unaccepted action, status should be Non verified" );
        }


        merchant.setNumberCheckoutRequested( dto.getNbCheckout() );
        merchant.setArticleImpotsNumber( dto.getAi() );
        merchant.setIdentityCardNumber( dto.getCni() );
        merchant.setRegistreCommerceNumber( dto.getRc() );
        merchant.setFiscalNumber( dto.getNif() );

        // Create merchants account, but first check if he has an account
        if( merchantAccountRepository.existsByMerchantIdAndMerchantDeletedFalse( merchant.getId() ) ) {
            throw new MPayAPIException( HttpStatus.CONFLICT, "Merchant already has an account, status should not be NON_VERIFIED" );
        }
        RibProcessor.setBankRepository( bankRepository );
        RibProcessor.setAgencyRepository( agencyRepository );
        MerchantAccount account = new MerchantAccount();
        account.setBalance( 0 );
        account.setMerchant( merchant );
        account.setAccountNumber( dto.getRib() );
        account.setBank( RibProcessor.extractBankFrom( dto.getRib() ) );
        account.setAccountStatus( true );

        //Set Status and save trace
        merchant.setStatus( MerchantStatus.FILLED_INFO );
        merchant.setBank(account.getBank());
        merchant.setAgency(RibProcessor.extractAgencyFrom(dto.getRib()));

        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setBank( RibProcessor.extractBankFrom( dto.getRib() ) );
        trace.setMerchant( merchant );
        trace.setUser( merchant.getUsername() );
        trace.setStatus( merchant.getStatus() );
        trace.setCreatedAt( new Date() );

        merchantStatusTraceRepository.save( trace );
        merchantAccountRepository.save( account );
        merchantRepository.save( merchant );

        MerchantResponseDto responseDto = modelMapper.map( merchant, MerchantResponseDto.class );

        responseDto.setRib( dto.getRib() );
        responseDto.setTerminalId( null );

        return responseDto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void blockMerchantAccount(Long id, BlockRequestDto dto, String usernameOfBlocker) {

        // Checking if merchant exists
        MerchantAccount account;
        if( merchantRepository.existsByIdAndDeletedFalse( id ) ) {
            account = merchantAccountRepository.findByMerchantId( id )
                    .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account", "Merchant id", id ) );
        } else throw new MPayAPIException( HttpStatus.NOT_FOUND, "Merchant does not exist or is deleted" );

        // Blocking merchant
        account.setAccountStatus( false );

        //Creating trace
        MerchantAccountBlockTrace trace = modelMapper.map( dto, MerchantAccountBlockTrace.class );
        trace.setAccount( account );
        User user = userRepository.findByUsername( usernameOfBlocker )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", usernameOfBlocker ) );
        trace.setCreatedBy( user );
        trace.setCreatedOn( new Date() );
        trace.setAccountStatus( false );
        merchantAccountBlockTraceRepository.save( trace );

        merchantAccountRepository.save( account );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void unBlockMerchantAccount(Long id, BlockRequestDto dto, String usernameOfBlocker) {

        // Checking if merchant exists
        MerchantAccount account;
        if( merchantRepository.existsByIdAndDeletedFalse( id ) ) {
            account = merchantAccountRepository.findByMerchantId( id )
                    .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account", "Merchant id", id ) );
        } else throw new MPayAPIException( HttpStatus.NOT_FOUND, "Merchant does not exist or is deleted" );

        // Blocking merchant
        account.setAccountStatus( true );

        //Creating trace
        MerchantAccountBlockTrace trace = modelMapper.map( dto, MerchantAccountBlockTrace.class );
        trace.setAccount( account );
        User user = userRepository.findByUsername( usernameOfBlocker )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", usernameOfBlocker ) );
        trace.setCreatedBy( user );
        trace.setCreatedOn( new Date() );
        trace.setAccountStatus( true );
        merchantAccountBlockTraceRepository.save( trace );

        merchantAccountRepository.save( account );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto putInProgress(Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        if( !merchant.getStatus().equals( MerchantStatus.FILLED_INFO ) && !merchant.getStatus().equals( MerchantStatus.REVIEW )  ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Forbidden action: Merchant current status must be 'FILLED_INFO' or 'REVIEW'" );
        }

        merchant.setStatus( MerchantStatus.IN_PROGRESS );
        merchantRepository.save( merchant );

        // Save merchant trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.IN_PROGRESS );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map( merchant, MerchantDto.class );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto demandReviewFile(Long id, String feedback) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        if( !merchant.getStatus().equals( MerchantStatus.IN_PROGRESS ) ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Forbidden action: Merchant current status must be 'IN_PROGRESS'" );
        }

        merchant.setStatus( MerchantStatus.REVIEW );
        merchantRepository.save( merchant );

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.REVIEW );
        trace.setFeedback( feedback );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map( merchant, MerchantDto.class );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto rejectMerchant(Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        // Check if merchant is in IN PROGRESS or SATIM_ACCEPTED
        if( !merchant.getStatus().equals( MerchantStatus.IN_PROGRESS ) && !merchant.getStatus().equals( MerchantStatus.SATIM_ACCEPTED ) ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Merchant status should be IN_PROGRESS or SATIM_ACCEPTED" );
        }


        merchant.setStatus( MerchantStatus.REJECTED );
        merchantRepository.save( merchant );

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.REJECTED );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map( merchant, MerchantDto.class );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto acceptMerchantByBank(Long id) {

        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is IN_PROGRESS
        if (!merchant.getStatus().equals(MerchantStatus.IN_PROGRESS)) {

            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be IN_PROGRESS");
        }

        // Set status to ACCEPTED
        merchant.setStatus(MerchantStatus.ACCEPTED);

        Merchant savedMerchant =  merchantRepository.save(merchant);

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.ACCEPTED );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map(savedMerchant, MerchantDto.class);
    }

    @Override
    public MerchantPageDto getAllMerchantsByFilterForSpecificBank(Integer page, Integer size, Long id, String firstName, String lastName, String phone, String registreCommerce, String numeroFiscal, String status, String callingUsername) {

        List<Merchant> merchants = merchantRepository.findByFilter( id, firstName, lastName, phone, registreCommerce, numeroFiscal, (status!=null ? MerchantStatus.valueOf( status ) : null) );
        // Check type of calling user
        User callingUser = userRepository.findByUsername(callingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", callingUsername));

        Bank bankOfCallingUser;

        if (callingUser.getUserType().equals(UserType.BANK_USER)
                || callingUser.getUserType().equals(UserType.BANK_ADMIN)) {

            UserBank user = userBankRepository.findByUsernameUsername(callingUsername)
                    .orElseThrow(() -> new ResourceNotFoundException("User Bank", "username", callingUsername));

            bankOfCallingUser = user.getBank();

            // Filter merchants by bank or agency
            List<Merchant> list = merchants.stream().filter(merchant -> {
                        if (merchant.getBank() != null) {
                            return (merchant.getBank().getId() == bankOfCallingUser.getId());
                        } else {
                            return false;
                        }
            }
            ).collect(Collectors.toList());

            return makePageDto(new PageImpl<>(list, PageRequest.of( page, size ), list.size()));

        } else if (callingUser.getUserType().equals(UserType.AGENCY_USER)
                || callingUser.getUserType().equals(UserType.AGENCY_ADMIN)) {

            UserAgency user = userAgencyRepository.findByUsernameUsername(callingUsername)
                    .orElseThrow(() -> new ResourceNotFoundException("User agency", "username", callingUsername));

            bankOfCallingUser = user.getAgency().getBank();
            // Filter merchants by bank or agency
            List<Merchant> list = merchants.stream().filter(merchant -> (merchant.getBank() != null) && merchant.getBank().equals(bankOfCallingUser)).toList();

            return makePageDto(new PageImpl<Merchant>(list,PageRequest.of( page, size ), list.size()));
        } else {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Calling user's type not authorized for this action");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto putToSatimReview(Long id, SatimReviewDto dto, String name) {

        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is ACCEPTED
        if (!merchant.getStatus().equals(MerchantStatus.ACCEPTED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be ACCEPTED");
        }

        // Set status to ACCEPTED
        merchant.setStatus(MerchantStatus.SATIM_REVIEW);

        Merchant savedMerchant =  merchantRepository.save(merchant);

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setFeedback(dto.getFeedback());
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.SATIM_REVIEW );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map(savedMerchant, MerchantDto.class);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto putToSatimAccepted(Long id, SatimAcceptDto dto, String name) {

        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is ACCEPTED
        if (!merchant.getStatus().equals(MerchantStatus.VALIDATED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be ACCEPTED");
        }

        licenseService.createLicense(merchant, dto);

        merchant.setStatus(MerchantStatus.SATIM_ACCEPTED);

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.SATIM_ACCEPTED );
        merchantStatusTraceRepository.save( trace );

        Merchant savedMerchant = merchantRepository.save(merchant);

        return modelMapper.map(savedMerchant, MerchantDto.class);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto putToSatimrejected(Long id, SatimAcceptDto dto, String name) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is ACCEPTED
        if (!merchant.getStatus().equals(MerchantStatus.ACCEPTED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be ACCEPTED");
        }

        merchant.setStatus(MerchantStatus.SATIM_REJECTED);

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.SATIM_REJECTED );
        merchantStatusTraceRepository.save( trace );

        Merchant savedMerchant = merchantRepository.save(merchant);

        return modelMapper.map(savedMerchant, MerchantDto.class);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MerchantDto verifyMerchant(Long id, String name) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is SATIM_ACCEPTED
        if (!merchant.getStatus().equals(MerchantStatus.SATIM_ACCEPTED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be SATIM_ACCEPTED");
        }

        merchant.setStatus(MerchantStatus.VERIFIED);
        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.VERIFIED );
        merchantStatusTraceRepository.save( trace );

        Merchant savedMerchant = merchantRepository.save(merchant);

        return modelMapper.map(savedMerchant, MerchantDto.class);

    }

    @Override
    public MerchantDto validateMerchant(Long id, AcceptMerchantDemandDto dto, String username) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", id));

        // Check if merchant is IN_PROGRESS
        if (!merchant.getStatus().equals(MerchantStatus.IN_PROGRESS)
        && !merchant.getStatus().equals(MerchantStatus.ACCEPTED)
        && !merchant.getStatus().equals(MerchantStatus.SATIM_REVIEW)) {

            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Merchant status should be IN_PROGRESS or ACCEPTED or SATIM_REVIEW");
        }

        // Check if BM and TM are correct
        BmFileCheckDto bmFileCheck = bmService.verifyFileContent(dto.getBmContent());
        if (!bmFileCheck.isAllCorrect()) {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Bm file content is not correct");
        }

        TmFileCheckDto tmFileCheck = tmService.verifyFileContent(dto.getTmContent());
        if (!tmFileCheck.isAllCorrect()) {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Tm file content is not correct");
        }

        // Finding creating bank user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));


        // Check if BM or TM exists before
        Bm existingBm = bmRepository.findByMerchantIdAndDeletedFalse(merchant.getId())
                .orElse(null);
        // If one BM exists then the TM exists as well, so we set them to deleted
        if (existingBm != null) {
            Tm existingTm = tmRepository.findByBmIdAndDeletedFalse(existingBm.getId())
                    .orElse(null);
            if (existingTm != null) existingTm.setDeleted(true);
            existingBm.setDeleted(true);
        }

        // Create a BM entity
        //
        Bm bm = new Bm();
        bm.setCreatedBy(user);
        bm.setCreatedOn(new Date());
        bm.setMerchant(merchant);
        bm.setContractNumberMerchantBM(bmFileCheck.getMerchantContractNumber().getValue());
        bm.setMerchantName(merchant.getLastName() + " " + merchant.getFirstName());
        bm.setMerchantSocialReason(bmFileCheck.getMerchantSocialReason().getValue());
        bm.setAgencyCode(bmFileCheck.getMerchantAgencyCode().getValue());
        bm.setDocumentTypeForBank(bmFileCheck.getMerchantIdDocumentType().getValue());
        bm.setExercisingYearNumber(bmFileCheck.getMerchantExperienceYears().getValue());
        bm.setFaxNumber(bmFileCheck.getMerchantFaxNumber().getValue());
        bm.setLine1TradeAddress(bmFileCheck.getAddressLine1().getValue());
        bm.setLine2TradeAddress(bmFileCheck.getAddressLine2().getValue());
        bm.setLine3Address(bmFileCheck.getAddressLine3().getValue());
        bm.setLine4Address(bmFileCheck.getAddressLine4().getValue());
        bm.setLine5Address(bmFileCheck.getAddressLine5().getValue());
        bm.setLine6Address(bmFileCheck.getAddressLine6().getValue());
        bm.setLine7Address(bmFileCheck.getAddressLine7().getValue());
        bm.setLine8Address(bmFileCheck.getAddressLine8().getValue());
        bm.setMerchantAgencyLabel(bmFileCheck.getMerchantAgencyLabel().getValue());
        bm.setMerchantMail(bmFileCheck.getMerchantEmailAddress().getValue());
        bm.setMerchantNif(bmFileCheck.getMerchantNif().getValue());
        bm.setMerchantMobilePhoneNumber(bmFileCheck.getMerchantMobileNumber().getValue());
        bm.setMerchantPhoneNumber(bmFileCheck.getMerchantPhoneNumber().getValue());
        bm.setMerchantSecondNameContact(bmFileCheck.getSecondContactName().getValue());
        bm.setMerchantTransactionThreshold(bmFileCheck.getMerchantThreshold().getValue());
        bm.setMerchantRib(bmFileCheck.getMerchantRib().getValue());
        bm.setPrincipleContactTitle(bmFileCheck.getPrincipalContractTitle().getValue());
        bm.setRcNumber(bmFileCheck.getRcNumber().getValue());
        bm.setTradeCategory(bmFileCheck.getTradeCategory().getValue());
        bm.setTradeLabel(bmFileCheck.getTradeLabel().getValue());
        bm.setWebSiteAdress(bmFileCheck.getWebsiteAddress().getValue());
        bm.setEnteteBm(dto.getBmContent().split("\n")[0]);
        bm.setDebutInfoBm(dto.getBmContent().split("\n")[1]);
        bm.setFinBm(dto.getBmContent().split("\n")[2]);

        bmRepository.save(bm);

        // Create a TM entity
        Tm tm = new Tm();
        tm.setBm(bm);
        tm.setCardType1(tmFileCheck.getCardType1().getValue());
        tm.setCardType(tmFileCheck.getCardType0().getValue());
        tm.setCardType2(tmFileCheck.getCardType2().getValue());
        tm.setCardType3(tmFileCheck.getCardType3().getValue());
        tm.setCardType4(tmFileCheck.getCardType4().getValue());
        tm.setCardType5(tmFileCheck.getCardType5().getValue());
        tm.setCardType6(tmFileCheck.getCardType6().getValue());
        tm.setLimit0(tmFileCheck.getLimit0().getValue());
        tm.setLimit1(tmFileCheck.getLimit1().getValue());
        tm.setLimit2(tmFileCheck.getLimit2().getValue());
        tm.setLimit3(tmFileCheck.getLimit3().getValue());
        tm.setLimit4(tmFileCheck.getLimit4().getValue());
        tm.setLimit5(tmFileCheck.getLimit5().getValue());
        tm.setLimit6(tmFileCheck.getLimit6().getValue());
        tm.setTerminalLine1Address(tmFileCheck.getAddressLine1().getValue());
        tm.setLine2Address(tmFileCheck.getAddressLine2().getValue());
        tm.setLine3Address(tmFileCheck.getAddressLine3().getValue());
        tm.setLine4Address(tmFileCheck.getAddressLine4().getValue());
        tm.setLine5Address(tmFileCheck.getAddressLine5().getValue());
        tm.setLine6Address(tmFileCheck.getAddressLine6().getValue());
        tm.setLine7Address(tmFileCheck.getAddressLine7().getValue());
        tm.setLine8Address(tmFileCheck.getAddressLine8().getValue());
        tm.setUpdateCardTypeForTerminal(tmFileCheck.getTerminalCardTypeUpdate().getValue());
        tm.setTrxWithdraw(tmFileCheck.getTrxRetrait().getValue());
        tm.setTrxSolde(tmFileCheck.getTrxSolde().getValue());
        tm.setTrxRemb(tmFileCheck.getTrxRemb().getValue());
        tm.setTrxPhone(tmFileCheck.getTrxTel().getValue());
        tm.setTrxPAutor(tmFileCheck.getTrxPAutor().getValue());
        tm.setTrxCashAdvancing(tmFileCheck.getTrxCashAdvance().getValue());
        tm.setTrxDebit(tmFileCheck.getTrxDebit().getValue());
        tm.setTrxBillPayment(tmFileCheck.getTrxPaiementFacture().getValue());
        tm.setTrxAnnul(tmFileCheck.getTrxAnnul().getValue());
        tm.setTerminalType(tmFileCheck.getTerminalType().getValue());
        tm.setTerminalPhoneNumber(tmFileCheck.getTerminalPhoneNumber().getValue());
        tm.setTerminalMobileNumber(tmFileCheck.getTerminalMobileNumber().getValue());
        tm.setTerminalMailAddress(tmFileCheck.getTerminalEmailAddress().getValue());
        tm.setTerminalLabel(tmFileCheck.getTerminalLabel().getValue());
        tm.setTerminalId(tmFileCheck.getTerminalId().getValue());
        tm.setTerminalFaxNumber(tmFileCheck.getTerminalFaxNumber().getValue());
        tm.setStartTime(tmFileCheck.getHourStart().getValue());
        tm.setEndTime(tmFileCheck.getHourEnd().getValue());
        tm.setEnteteTm(dto.getTmContent().split("\n")[0]);
        tm.setDebutInfoTm(dto.getTmContent().split("\n")[1]);
        tm.setFinTm(dto.getTmContent().split("\n")[2]);


        tmRepository.save(tm);

        // Set status to ACCEPTED
        merchant.setStatus(MerchantStatus.VALIDATED);

        Merchant savedMerchant =  merchantRepository.save(merchant);

        // Save trace
        MerchantAccount account = merchantAccountRepository.findByMerchantId( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant Account ", "merchant id", id ) );
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setBank( account.getBank() );
        trace.setUser( merchant.getUsername() );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.VALIDATED );
        merchantStatusTraceRepository.save( trace );

        return modelMapper.map(savedMerchant, MerchantDto.class);
    }
}
