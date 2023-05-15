package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.BlogAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.payload.UserBankDto;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.UserBankRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserAgencyService;
import com.springboot.mpaybackend.service.UserBankService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBankServiceImpl implements UserBankService {


    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;
    UserBankRepository userBankRepository;
    BankRepository bankRepository;
    UserRepository userRepository;

    public UserBankServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserBankRepository userBankRepository, BankRepository bankRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userBankRepository = userBankRepository;
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserBankDto getUserBank(Long id) {
        UserBank userBank = userBankRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "id", id ) );

        return modelMapper.map( userBank, UserBankDto.class );
    }

    @Override
    public UserBankDto getUserBankByUsername(String username) {
        UserBank userBank = userBankRepository.findByUsernameUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "username", username ) );

        return modelMapper.map( userBank, UserBankDto.class );
    }

    @Override
    public List<UserBankDto> getUsersBankByBankId(Long bank) {
        List<UserBank> userBank = userBankRepository.findAllByBankId( bank );

        return userBank.stream().map( (user -> modelMapper.map( user, UserBankDto.class )) )
                .collect( Collectors.toList() );

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserBankDto addUserBank(UserBankDto dto, String username) {

        modelMapper.getConfiguration().setMatchingStrategy( MatchingStrategies.STRICT );

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new BlogAPIException( HttpStatus.BAD_REQUEST, "Username already exists!.");
        }


        User user = new User();
        user.setUsername( dto.getUsername() );
        user.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        user.setPhone( dto.getPhone() );
        user.setFirstConnexion( true );
        user.setUserType( UserType.BANK_USER );
        System.out.println(dto.getUsername());
        userRepository.save( user );

        UserBank userBank = modelMapper.map( dto, UserBank.class );

        Bank bank = bankRepository.findById( dto.getBankId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", dto.getBankId() ) );
        userBank.setBank( bank );

        User creatingUser = userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", username ) );
        userBank.setCreatedBy( creatingUser );
        userBank.setCreatedAt( new Date() );
        userBank.setUpdatedBy( creatingUser );
        userBank.setUpdatedAt( new Date() );
        userBank.setUserType( UserType.BANK_USER );
        userBank.setUsername( user );

        UserBank savedBankUer = userBankRepository.save( userBank );
        return modelMapper.map( savedBankUer, UserBankDto.class );
    }

    @Override
    public List<UserBankDto> getAllUserBanks() {
        List<UserBank> usersBank = userBankRepository.findAll();

        return usersBank.stream().map( (user -> modelMapper.map( user, UserBankDto.class )) ).collect( Collectors.toList());
    }

    @Override
    public UserBankDto updateUserBank(UserBankDto dto, Long id, String updatingUsername) {
        UserBank userBank = userBankRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "id", id ) );


        User updatingUser = userRepository.findByUsername( updatingUsername )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", updatingUsername ) );

        userBank.setUpdatedBy( updatingUser );
        userBank.setUpdatedAt( new Date() );

        if( dto.getEmail() != null ) userBank.setEmail( dto.getEmail() );
        if( dto.getFirstName() != null ) userBank.setFirstName( dto.getFirstName() );
        if( dto.getLastName() != null ) userBank.setLastName( dto.getLastName() );
        if( dto.getPhone() != null ) userBank.setPhone( dto.getPhone() );
        if( dto.getBankId() != null ) {
            Bank bank = bankRepository.findById( dto.getBankId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", dto.getBankId() ) );
            userBank.setBank( bank );
        }

        UserBank savedUserBank = userBankRepository.save(userBank);
        return modelMapper.map( savedUserBank, UserBankDto.class );
    }

    @Override
    public void deleteUserBank(Long id) {
        UserBank userBank = userBankRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "id", id ) );

        userBankRepository.delete( userBank );
    }
}
