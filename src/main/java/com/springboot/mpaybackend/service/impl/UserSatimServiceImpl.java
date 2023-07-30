package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserSatim;
import com.springboot.mpaybackend.entity.UserType;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.MerchantPageDto;
import com.springboot.mpaybackend.payload.MerchantResponseDto;
import com.springboot.mpaybackend.payload.UserSatimDto;
import com.springboot.mpaybackend.payload.UserSatimPageDto;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.UserBankRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.repository.UserSatimRepository;
import com.springboot.mpaybackend.service.UserAgencyService;
import com.springboot.mpaybackend.service.UserSatimService;
import jakarta.transaction.Transactional;
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
public class UserSatimServiceImpl implements UserSatimService {

    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;
    UserSatimRepository userSatimRepository;
    UserRepository userRepository;

    public UserSatimServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserSatimRepository userSatimRepository, UserRepository userRepository) {

        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userSatimRepository = userSatimRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserSatimDto addUser(UserSatimDto dto, String creatingUsername) {

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "Username already exists!.");
        }

        User user = new User();
        user.setUsername( dto.getUsername() );
        user.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        user.setPhone( dto.getPhone() );
        user.setFirstConnexion( true );
        user.setUserType( UserType.SATIM );
        userRepository.save( user );

        UserSatim userSatim = modelMapper.map( dto, UserSatim.class );

        User creatingUser = userRepository.findByUsername( creatingUsername )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", creatingUsername ) );
        userSatim.setCreatedBy( creatingUser );
        userSatim.setCreatedAt( new Date() );
        userSatim.setUpdatedBy( creatingUser );
        userSatim.setUpdatedAt( new Date() );

        userSatim.setUsername( user );

        UserSatim savedUserSatim = userSatimRepository.save( userSatim );
        UserSatimDto dtoResponse = modelMapper.map( savedUserSatim, UserSatimDto.class );
        dtoResponse.setUsername( user.getUsername() );

        return dtoResponse;
    }

    @Override
    public List<UserSatimDto> getAllUsers() {

        List<UserSatim> userSatims = userSatimRepository.findAllByDeletedFalse();

        return userSatims.stream().map(
                userSatim -> {
                    UserSatimDto dto = modelMapper.map( userSatim, UserSatimDto.class );
                    dto.setPassword( null );
                    return dto;
                }
        ).collect( Collectors.toList());
    }

    @Override
    public UserSatimPageDto getAllUsersByFilter(Long id, Integer page, Integer size, String name, String phone) {

        Page<UserSatim> satims = userSatimRepository.findByFilter( PageRequest.of( page, size ), id, name, name, phone );

        return makePageDto( satims );
    }

    @Override
    public UserSatimDto getUser(Long id) {
        UserSatim userSatim = userSatimRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User Satim", "id", id ) );

        return modelMapper.map( userSatim, UserSatimDto.class );
    }

    @Override
    public UserSatimDto getUserByUsername(String username) {
        UserSatim userSatim = userSatimRepository.findByUsernameUsernameAndDeletedFalse( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User Satim", "username", username ) );

        return modelMapper.map( userSatim, UserSatimDto.class );
    }

    @Override
    public UserSatimDto updateUser(UserSatimDto dto, Long id, String updatingUsername) {
        UserSatim userSatim = userSatimRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User Satim", "id", id ) );

        dto.setUsername( null );
        dto.setPassword( null );

        UserSatim updatedUser = modelMapper.map( dto, UserSatim.class );
        updatedUser.setId( null );
        modelMapper.map(updatedUser, userSatim );

        User updatingUser = userRepository.findByUsername( updatingUsername )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", updatingUsername ) );
        userSatim.setUpdatedBy( updatingUser );
        userSatim.setUpdatedAt( new Date() );

        UserSatim savedUserSatim = userSatimRepository.save( userSatim );

        return modelMapper.map( savedUserSatim, UserSatimDto.class );
    }

    @Override
    public void deleteUserBank(Long id) {
        UserSatim userSatim = userSatimRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User Satim", "id", id ) );
        userSatim.setDeleted( true );

        userSatimRepository.save( userSatim );
    }

    private UserSatimPageDto makePageDto(Page<UserSatim> satims) {
        List<UserSatimDto> dto = satims.stream().map( e -> modelMapper.map( e, UserSatimDto.class ) ).collect( Collectors.toList() );

        UserSatimPageDto pageDto = new UserSatimPageDto();
        pageDto.setUserPage( dto );
        pageDto.setCount( satims.getTotalElements() );
        return pageDto;
    }
}
