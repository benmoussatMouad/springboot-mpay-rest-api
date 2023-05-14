package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Agency;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserAgency;
import com.springboot.mpaybackend.entity.UserType;
import com.springboot.mpaybackend.exception.BlogAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.UserAgencyRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserAgencyService;
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
    public UserAgencyDto getUserAgency(Long id) {

        UserAgency userAgency = userAgencyRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "id", id ) );

        return modelMapper.map( userAgency, UserAgencyDto.class );
    }

    @Override
    public UserAgencyDto getUserAgencyByUsername(String username) {
        UserAgency userAgency = userAgencyRepository.findByUsernameUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "username", username ) );
        return modelMapper.map( userAgency, UserAgencyDto.class );
    }

    @Override
    public List<UserAgencyDto> getUsersAgencyByAgency(Long agencyId) {
        List<UserAgency> usersAgency = userAgencyRepository.findAllByAgencyId( agencyId );

        return usersAgency.stream().map( (userAgency -> modelMapper.map( userAgency, UserAgencyDto.class )) ).collect( Collectors.toList() );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserAgencyDto addUserAgency(UserAgencyDto dto, String username) {

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
        user.setUserType( "AGENCY_USER" );
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
    public List<UserAgencyDto> getUsersAgency() {
        List<UserAgency> userAgencies = userAgencyRepository.findAll();

        return userAgencies.stream().map( (userAgency -> modelMapper.map( userAgency, UserAgencyDto.class )) ).collect( Collectors.toList());
    }

    @Override
    public UserAgencyDto updateUserAgency(UserAgencyDto dto, Long id, String updatingUsername) {
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

        UserAgency savedUserAgency = userAgencyRepository.save( userAgency );
        return modelMapper.map( savedUserAgency, UserAgencyDto.class );
    }


    @Override
    public void deleteUserAgency(Long id) {
        UserAgency userAgency = userAgencyRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAgency", "id", id ) );

        userAgencyRepository.delete( userAgency );
    }

    // TODO: add putting agency to user, or removing agencies
}
