package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.BlogAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.UserBankRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserBankService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    public UserBankResponseDto getUserBank(Long id) {
        UserBank userBank = userBankRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "id", id ) );

        UserBankResponseDto responseDto = modelMapper.map( userBank, UserBankResponseDto.class );
        responseDto.setUsername( userBank.getUsername().getUsername() );

        return responseDto;
    }

    @Override
    public UserBankResponseDto getUserBankByUsername(String username) {
        UserBank userBank = userBankRepository.findByUsernameUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "UserBank", "username", username ) );

        UserBankResponseDto responseDto = modelMapper.map( userBank, UserBankResponseDto.class );
        responseDto.setUsername( userBank.getUsername().getUsername() );

        return responseDto;
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

        if( Objects.equals( dto.getUserType(), "BANK_ADMIN" ) || Objects.equals( dto.getUserType(), "BANK_USER" ) ) {
            userBank.setUserType( UserType.valueOf( dto.getUserType() ) );
        } else
            throw new BlogAPIException( HttpStatus.BAD_REQUEST, "Wrong user type, should be BANK_USER or BANK_ADMIN" );

        userBank.setUsername( user );

        UserBank savedBankUser = userBankRepository.save( userBank );
        UserBankDto responseDto = modelMapper.map( savedBankUser, UserBankDto.class );
        responseDto.setUsername( user.getUsername() );
        responseDto.setBankId( savedBankUser.getBank().getId() );

        return responseDto;
    }

    @Override
    public List<UserBankDto> getAllUserBanks() {
        List<UserBank> usersBank = userBankRepository.findAll();

        return usersBank.stream().map( (user -> modelMapper.map( user, UserBankDto.class )) ).collect( Collectors.toList());
    }

    @Override
    public UserBankPageDto getAllUserBank(int page, int size) {
        Page<UserBank> users = userBankRepository.findAll( PageRequest.of( page, size ) );

        List<UserBankResponseDto> userDtos = users.stream().map( (userBank -> {

            UserBankResponseDto dto = modelMapper.map( userBank, UserBankResponseDto.class );
            return dto;
        }) ).toList();

        UserBankPageDto userBankPageDto = new UserBankPageDto();

        userBankPageDto.setCount( users.getTotalElements() );
        userBankPageDto.setUserPage( userDtos );

        return userBankPageDto;
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

    @Override
    public UserBankPageDto getAllUserBankByFilter(Integer page, Integer size, String name, String phone, String userType, Long bankId) {

        if( name == null ) {
            if( phone == null ) {
                if( userType == null ) {
                    if( bankId == null ) {
                        return this.getAllUserBank( page, size );
                    } else {
                        Page<UserBank> userBankPage = userBankRepository.findByBankId( PageRequest.of( page, size ), bankId );
                        return pageDtoOf( userBankPage );
                    }
                } else {
                    if( bankId == null ) {
                        Page<UserBank> userBankPage = userBankRepository.findByUserType( PageRequest.of( page, size ), UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    } else {
                        Page<UserBank> userBankPage = userBankRepository.findByBankIdAndUserType( PageRequest.of( page, size ), bankId, UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    }
                }
            } else {
                if( userType == null ) {
                    if( bankId == null ) {
                        Page<UserBank> userBankPage = userBankRepository.findByPhoneContaining( PageRequest.of( page, size ), phone );
                        return pageDtoOf( userBankPage );
                    } else {
                        Page<UserBank> userBankPage = userBankRepository.findByPhoneContainingAndBankId( PageRequest.of( page, size ), phone, bankId );
                        return pageDtoOf( userBankPage );
                    }
                } else {
                    if( bankId == null ) {// phone AND usertype only
                        Page<UserBank> userBankPage = userBankRepository.findByPhoneContainingAndUserType( PageRequest.of( page, size ), phone, UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    } else {
                        // phone AND userType AND bankId
                        Page<UserBank> userBankPage = userBankRepository.findByPhoneContainingAndBankIdAndUserType( PageRequest.of( page, size ), phone, bankId, UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    }
                }
            }
        } else { // name AND
            if( phone == null ) {
                if( userType == null ) {
                    if( bankId == null ) {
                        Page<UserBank> userBankPage = userBankRepository.findByFirstNameContainingOrLastNameContaining( PageRequest.of( page, size ), name, name );
                        return pageDtoOf( userBankPage );
                    } else {
                        Page<UserBank> userBankPage = userBankRepository.findByFirstNameContainingOrLastNameContainingAndBankId( PageRequest.of( page, size ), name, name, bankId );
                        return pageDtoOf( userBankPage );
                    }
                } else { // name AND userType
                    if( bankId == null ) {
                        Page<UserBank> userBankPage = userBankRepository.findByFirstNameContainingOrLastNameContainingAndUserType( PageRequest.of( page, size ), name, name, UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    } else {
                        // name AND userType AND bankId
                        Page<UserBank> userBankPage = userBankRepository.findByFirstNameContainingOrLastNameContainingAndBankIdAndUserType( PageRequest.of( page, size ), name, name, bankId, UserType.valueOf( userType ) );
                        return pageDtoOf( userBankPage );
                    }

                }
            } else {
                if( userType == null ) {
                    Page<UserBank> userBankPage;
                    if( bankId == null ) { // name AND phone
                        userBankPage = userBankRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContaining( PageRequest.of( page, size ), phone, name, name );
                    } else {
                        // name AND phone AND bankId
                        userBankPage = userBankRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndBankId( PageRequest.of( page, size ), phone, name, name, bankId );
                    }
                    return pageDtoOf( userBankPage );
                } else {
                    Page<UserBank> userBankPage;
                    if( bankId == null ) {
                        // name AND phone AND userType
                        userBankPage = userBankRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserType( PageRequest.of( page, size ), phone, name, name, UserType.valueOf( userType ) );
                    } else {
                        // name AND phone AND userType AND bankId
                        userBankPage = userBankRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndBankIdAndUserType( PageRequest.of( page, size ), phone, name, name, bankId, UserType.valueOf( userType ) );
                    }
                    return pageDtoOf( userBankPage );
                }
            }
        }
    }

    private UserBankPageDto pageDtoOf(Page<UserBank> userBankPage) {
        List<UserBankResponseDto> userDtos = userBankPage.stream().map( (userBank -> modelMapper.map( userBank, UserBankResponseDto.class )) ).toList();

        UserBankPageDto userBankPageDto = new UserBankPageDto();

        userBankPageDto.setCount( userBankPage.getTotalElements() );
        userBankPageDto.setUserPage( userDtos );

        return userBankPageDto;
    }
}
