package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.BlogAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.UserAdminDto;
import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.repository.UserAdminRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserAdminService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminServiceImpl implements UserAdminService {
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserAdminRepository userAdminRepository;

    public UserAdminServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, UserAdminRepository userAdminRepository) {

        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userAdminRepository = userAdminRepository;
    }

    @Override
    public UserAdminDto getUserAdmin(Long id) {
        UserAdmin admin = userAdminRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User Admin", "id", id ) );
        return modelMapper.map( admin, UserAdminDto.class );
    }

    @Override
    public UserAdminDto getUserAdminByUsername(String username) {
        UserAdmin admin = userAdminRepository.findByUsernameUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User Admin", "username", username ) );
        return modelMapper.map( admin, UserAdminDto.class );
    }

    @Override
    public List<UserAdminDto> getAllUserAdmin() {
        List<UserAdmin> admins = userAdminRepository.findAll();

        return admins.stream().map( (user -> modelMapper.map( user, UserAdminDto.class )) ).collect( Collectors.toList());

    }

    @Override
    public UserAdminDto updateUserAdmin(UserAdminDto dto, Long id) {
        UserAdmin admin = userAdminRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "UserAdmin", "id", id ) );

        if( dto.getEmail() != null ) admin.setEmail( dto.getEmail() );
        if( dto.getFirstName() != null ) admin.setFirstName( dto.getFirstName() );
        if( dto.getLastName() != null ) admin.setLastName( dto.getLastName() );
        if( dto.getPhone() != null ) admin.setPhone( dto.getPhone() );

        UserAdmin savedAdmin = userAdminRepository.save( admin );
        UserAdminDto adminDto = modelMapper.map( savedAdmin, UserAdminDto.class );
        adminDto.setUsername( savedAdmin.getUsername().getUsername() );
        return adminDto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserAdminDto addUserAdmin(UserAdminDto dto) {
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
        user.setUserType( UserType.ADMIN );
        System.out.println(dto.getUsername());
        userRepository.save( user );

        UserAdmin admin = modelMapper.map( dto, UserAdmin.class );
        admin.setUserType( UserType.ADMIN );
        admin.setUsername( user );

        UserAdmin savedAdmin = userAdminRepository.save( admin );
        UserAdminDto responseDto = modelMapper.map( savedAdmin, UserAdminDto.class );
        responseDto.setUsername( user.getUsername() );

        return responseDto;
    }


    @Override
    public void deleteUserAdmin(Long id) {
        userAdminRepository.deleteById( id );
    }
}
