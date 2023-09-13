package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.UserAgencyRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserAgencyService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserAgencyServiceImpl implements UserAgencyService {

    UserAgencyRepository userAgencyRepository;
    UserRepository userRepository;
    AgencyRepository agencyRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;

    public UserAgencyServiceImpl(UserAgencyRepository userAgencyRepository, UserRepository userRepository, AgencyRepository agencyRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userAgencyRepository = userAgencyRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;


    }

    @Override
    public UserAgencyResponseDto getUserAgency(Long id) {

        UserAgency userAgency = userAgencyRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "id", id ) );

        return modelMapper.map( userAgency, UserAgencyResponseDto.class );
    }

    @Override
    public UserAgencyResponseDto getUserAgencyByUsername(String username) {
        UserAgency userAgency = userAgencyRepository.findByUsernameUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "username", username ) );
        return modelMapper.map( userAgency, UserAgencyResponseDto.class );
    }

    @Override
    public List<UserAgencyResponseDto> getUsersAgencyByAgency(Long agencyId) {
        List<UserAgency> usersAgency = userAgencyRepository.findAllByAgencyId( agencyId );

        return usersAgency.stream().map( (userAgency -> modelMapper.map( userAgency, UserAgencyResponseDto.class )) ).collect( Collectors.toList() );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserAgencyDto addUserAgency(UserAgencyDto dto, String username) {

        modelMapper.getConfiguration().setMatchingStrategy( MatchingStrategies.STRICT );

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "Username already exists!.");
        }


        User user = new User();
        user.setUsername( dto.getUsername() );
        user.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        user.setPhone( dto.getPhone() );
        user.setFirstConnexion( true );
        user.setUserType( UserType.AGENCY_USER );
        System.out.println(dto.getUsername());
        userRepository.save( user );

        UserAgency userAgency = modelMapper.map( dto, UserAgency.class );

        Agency agency = agencyRepository.findById( dto.getAgencyId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Agency", "id", dto.getAgencyId() ) );
        userAgency.setAgency( agency );

        User creatingUser = userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", username ) );
        userAgency.setCreatedBy( creatingUser );
        userAgency.setCreatedAt( new Date() );
        userAgency.setUpdatedBy( creatingUser );
        userAgency.setUpdatedAt( new Date() );
        userAgency.setUserType( UserType.AGENCY_USER );
        userAgency.setUsername( user );

        UserAgency savedUserAgency = userAgencyRepository.save( userAgency );
        return modelMapper.map( savedUserAgency, UserAgencyDto.class );
    }

    @Override
    public List<UserAgencyResponseDto> getUsersAgency() {
        List<UserAgency> userAgencies = userAgencyRepository.findAll();

        return userAgencies.stream().map( (userAgency -> modelMapper.map( userAgency, UserAgencyResponseDto.class )) ).collect( Collectors.toList());
    }

    @Override
    public UserAgencyResponseDto updateUserAgency(UserAgencyDto dto, Long id, String updatingUsername) {
        UserAgency userAgency = userAgencyRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "id", id ) );


        User updatingUser = userRepository.findByUsername( updatingUsername )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", updatingUsername ) );

        userAgency.setUpdatedBy( updatingUser );
        userAgency.setUpdatedAt( new Date() );

        if( dto.getEmail() != null ) userAgency.setEmail( dto.getEmail() );
        if( dto.getFirstName() != null ) userAgency.setFirstName( dto.getFirstName() );
        if( dto.getLastName() != null ) userAgency.setLastName( dto.getLastName() );
        if( dto.getPhone() != null ) userAgency.setPhone( dto.getPhone() );
        if( dto.getAgencyId() != null ) {
            Agency agency = agencyRepository.findById( dto.getAgencyId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "Agency", "id", dto.getAgencyId() ) );
            userAgency.setAgency( agency );
        }

        UserAgency savedUserAgency = userAgencyRepository.save( userAgency );
        UserAgencyResponseDto userAgencyDto = modelMapper.map( savedUserAgency, UserAgencyResponseDto.class );
        // Not to change the username, but to set a String instead when sending DTO
        userAgencyDto.setUsername( userAgency.getUsername().getUsername() );
        return userAgencyDto;
    }


    @Override
    public void deleteUserAgency(Long id) {
        UserAgency userAgency = userAgencyRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "id", id ) );

        userAgencyRepository.delete( userAgency );
    }

    @Override
    public UserAgencyPageDto getAllUserAgency(Integer page, Integer size) {
        Page<UserAgency> users = userAgencyRepository.findAll( PageRequest.of( page, size ) );

        List<UserAgencyResponseDto> userDtos = users.stream().map( (user -> modelMapper.map( user, UserAgencyResponseDto.class )) ).toList();

        UserAgencyPageDto userAgencyPageDto = new UserAgencyPageDto();

        userAgencyPageDto.setCount( users.getTotalElements() );
        userAgencyPageDto.setUserPage( userDtos );

        return userAgencyPageDto;
    }

    @Override
    public UserAgencyPageDto getAllUserAgencyByFilter(Long id, Integer page, Integer size, String name, String phone, String userType, Long bankId, Long agencyId) {
        UserAgencyPageDto toReturn = null;
        System.out.printf( "id in service is : " + id );
        if( id != null ) {
            // TODO: FIX string contain
            UserAgency userAgency = null;
            if( userAgencyRepository.existsById( id ) ) {
                userAgency = userAgencyRepository.findById( id )
                        .orElseThrow( () -> new ResourceNotFoundException( "Agency User", "id", id ) );
                if( userAgency.getPhone().contains( Objects.toString( phone, "" ) )
                        && userAgency.getAgency().getId().equals( (agencyId != null ? agencyId : userAgency.getAgency().getId()) )
                        && userAgency.getUserType().equals( (userType !=null ? UserType.valueOf( userType ) : userAgency.getUserType()) )
                        && userAgency.getFirstName().contains( Objects.toString( name, "" ) )
                        && userAgency.getLastName().contains( Objects.toString( name, "" ) )
                        && userAgency.getAgency().getBank().getId().equals( (bankId != null ? bankId : userAgency.getAgency().getBank().getId() ) ) ) {

                    List<UserAgencyResponseDto> dtos = new ArrayList<UserAgencyResponseDto>();
                    dtos.add( modelMapper.map( userAgency, UserAgencyResponseDto.class ) );
                    UserAgencyPageDto userAgencyPageDto = new UserAgencyPageDto();
                    userAgencyPageDto.setCount( 1L );
                    userAgencyPageDto.setUserPage( dtos );

                    toReturn = userAgencyPageDto;
                } else {
                    UserAgencyPageDto dtos = new UserAgencyPageDto();
                    dtos.setCount( 0L );
                    dtos.setUserPage( new ArrayList<>() );
                    toReturn = dtos;
                }
            } else {
                UserAgencyPageDto dtos = new UserAgencyPageDto();
                dtos.setCount( 0L );
                dtos.setUserPage( new ArrayList<>() );
                toReturn = dtos;
            }
        } else if( agencyId == null ) {

            if( name == null ) {
                if( phone == null ) {
                    if( userType == null ) {
                        if( bankId == null ) {
                            toReturn = this.getAllUserAgency( page, size );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByAgencyBankId( PageRequest.of( page, size ), bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByUserType( PageRequest.of( page, size ), UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByAgencyBankIdAndUserType( PageRequest.of( page, size ), bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    }
                } else {
                    if( userType == null ) {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContaining( PageRequest.of( page, size ), phone );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndAgencyBankId( PageRequest.of( page, size ), phone, bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else {
                        if( bankId == null ) {// phone AND usertype only
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndUserType( PageRequest.of( page, size ), phone, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            // phone AND userType AND bankId
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndAgencyBankIdAndUserType( PageRequest.of( page, size ), phone, bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    }
                }
            } else { // name AND
                if( phone == null ) {
                    if( userType == null ) {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContaining( PageRequest.of( page, size ), name, name );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndAgencyBankId( PageRequest.of( page, size ), name, name, bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else { // name AND userType
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndUserType( PageRequest.of( page, size ), name, name, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            // name AND userType AND bankId
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndAgencyBankIdAndUserType( PageRequest.of( page, size ), name, name, bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }

                    }
                } else {
                    if( userType == null ) {
                        Page<UserAgency> userBankPage;
                        if( bankId == null ) { // name AND phone
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContaining( PageRequest.of( page, size ), phone, name, name );
                        } else {
                            // name AND phone AND bankId
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyBankId( PageRequest.of( page, size ), phone, name, name, bankId );
                        }
                        toReturn = pageDtoOf( userBankPage );
                    } else {
                        Page<UserAgency> userBankPage;
                        if( bankId == null ) {
                            // name AND phone AND userType
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserType( PageRequest.of( page, size ), phone, name, name, UserType.valueOf( userType ) );
                        } else {
                            // name AND phone AND userType AND bankId
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyBankIdAndUserType( PageRequest.of( page, size ), phone, name, name, bankId, UserType.valueOf( userType ) );
                        }
                        toReturn = pageDtoOf( userBankPage );
                    }
                }
            }
        } else {
            if( name == null ) {
                if( phone == null ) {
                    if( userType == null ) {
                        if( bankId == null ) {
                            toReturn = pageDtoOf( userAgencyRepository.findByAgencyId( PageRequest.of( page, size ), agencyId ) );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByAgencyIdAndAgencyBankId( PageRequest.of( page, size ), agencyId, bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByAgencyIdAndUserType( PageRequest.of( page, size ), agencyId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByAgencyIdAndAgencyBankIdAndUserType( PageRequest.of( page, size ), agencyId, bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    }
                } else {
                    if( userType == null ) {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndAgencyId( PageRequest.of( page, size ), phone, agencyId );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndAgencyIdAndAgencyBankId( PageRequest.of( page, size ), phone, agencyId, bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else {
                        if( bankId == null ) {// phone AND usertype only
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndUserTypeAndAgencyId( PageRequest.of( page, size ), phone, UserType.valueOf( userType ), agencyId );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            // phone AND userType AND bankId
                            Page<UserAgency> userBankPage = userAgencyRepository.findByPhoneContainingAndAgencyIdAndAgencyBankIdAndUserType( PageRequest.of( page, size ), phone, agencyId, bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    }
                }
            } else { // name AND
                if( phone == null ) {
                    if( userType == null ) {
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndAgencyId( PageRequest.of( page, size ), name, name, agencyId );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankId( PageRequest.of( page, size ), name, name, agencyId, bankId );
                            toReturn = pageDtoOf( userBankPage );
                        }
                    } else { // name AND userType
                        if( bankId == null ) {
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndUserTypeAndAgencyId( PageRequest.of( page, size ), name, name, UserType.valueOf( userType ), agencyId );
                            toReturn = pageDtoOf( userBankPage );
                        } else {
                            // name AND userType AND bankId
                            Page<UserAgency> userBankPage = userAgencyRepository.findByFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankIdAndUserType( PageRequest.of( page, size ), name, name, agencyId, bankId, UserType.valueOf( userType ) );
                            toReturn = pageDtoOf( userBankPage );
                        }

                    }
                } else {
                    if( userType == null ) {
                        Page<UserAgency> userBankPage;
                        if( bankId == null ) { // name AND phone
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyId( PageRequest.of( page, size ), phone, name, name, agencyId );
                        } else {
                            // name AND phone AND bankId
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankId( PageRequest.of( page, size ), phone, name, name, agencyId, bankId );
                        }
                        toReturn = pageDtoOf( userBankPage );
                    } else {
                        Page<UserAgency> userBankPage;
                        if( bankId == null ) {
                            // name AND phone AND userType
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserTypeAndAgencyId( PageRequest.of( page, size ), phone, name, name, UserType.valueOf( userType ), agencyId );
                        } else {
                            // name AND phone AND userType AND bankId
                            userBankPage = userAgencyRepository.findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankIdAndUserType( PageRequest.of( page, size ), phone, name, name, agencyId, bankId, UserType.valueOf( userType ) );
                        }
                        toReturn = pageDtoOf( userBankPage );
                    }
                }
            }
        }
        return toReturn;
    }

    @Override
    public boolean existsById(Long id) {
        return userAgencyRepository.existsById( id );
    }


    private UserAgencyPageDto pageDtoOf(Page<UserAgency> userBankPage) {
        List<UserAgencyResponseDto> userDtos = userBankPage.stream().map( 
            (userBank -> modelMapper.map( userBank, UserAgencyResponseDto.class )) ).toList();

        UserAgencyPageDto userBankPageDto = new UserAgencyPageDto();

        userBankPageDto.setCount( userBankPage.getTotalElements() );
        userBankPageDto.setUserPage( userDtos );

        return userBankPageDto;
    }
}
