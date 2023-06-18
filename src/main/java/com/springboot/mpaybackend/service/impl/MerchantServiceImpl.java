package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.service.MerchantService;
import com.springboot.mpaybackend.utils.RibProcessor;
import jakarta.transaction.Transactional;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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

    public MerchantServiceImpl(MerchantRepository merchantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, WilayaRepository wilayaRepository, MerchantAccountRepository merchantAccountRepository, BankRepository bankRepository, MerchantStatusTraceRepository merchantStatusTraceRepository, MerchantLicenseRepository merchantLicenseRepository, MerchantAccountBlockTraceRepository merchantAccountBlockTraceRepository) {
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
    public MerchantResponseDto addMerchant(MerchantDto dto, Boolean byBankUser) {

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
        if( !byBankUser ) {
            MerchantStatusTrace trace = new MerchantStatusTrace();
            trace.setMerchant( merchant );
            trace.setCreatedAt( new Date() );
            trace.setUser( user );
            trace.setStatus( MerchantStatus.NON_VERIFIED );

            merchantStatusTraceRepository.save( trace );
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

        //FIXME
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
        MerchantAccount account = new MerchantAccount();
        account.setBalance( 0 );
        account.setMerchant( merchant );
        account.setAccountNumber( dto.getRib() );
        account.setBank( RibProcessor.extractBankFrom( dto.getRib() ) );
        account.setAccountStatus( true );

        //Set Status and save trace
        merchant.setStatus( MerchantStatus.FILLED_INFO );

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

        // Check if merchant is in IN PROGRESS
        if( !merchant.getStatus().equals( MerchantStatus.IN_PROGRESS ) ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Merchant status should be IN_PROGRESS" );
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
}
