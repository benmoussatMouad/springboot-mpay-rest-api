package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.BlogAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.MerchantDto;
import com.springboot.mpaybackend.payload.MerchantPageDto;
import com.springboot.mpaybackend.payload.MerchantResponseDto;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.service.MerchantService;
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

    public MerchantServiceImpl(MerchantRepository merchantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, WilayaRepository wilayaRepository, MerchantAccountRepository merchantAccountRepository, BankRepository bankRepository, MerchantStatusTraceRepository merchantStatusTraceRepository) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.wilayaRepository = wilayaRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.bankRepository = bankRepository;
        this.merchantStatusTraceRepository = merchantStatusTraceRepository;

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
            throw new BlogAPIException( HttpStatus.BAD_REQUEST, "Phone already exists.");
        }

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username already exists.");
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
        }

        Merchant savedMerchant = merchantRepository.save( merchant );

        return modelMapper.map( savedMerchant, MerchantResponseDto.class );
    }

    @Override
    public MerchantResponseDto getMerchant(Long id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", id ) );

        return modelMapper.map( merchant, MerchantResponseDto.class );
    }

    @Override
    public MerchantResponseDto getMerchantByUsername(String username) {
        Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse( username )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "username", username ) );

        return modelMapper.map( merchant, MerchantResponseDto.class );
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
}
