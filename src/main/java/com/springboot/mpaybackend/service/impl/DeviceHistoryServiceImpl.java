package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.DeviceHistory;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.payload.DeviceHistoryDto;
import com.springboot.mpaybackend.repository.DeviceHistoryRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.repository.WilayaRepository;
import com.springboot.mpaybackend.service.DeviceHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeviceHistoryServiceImpl implements DeviceHistoryService {

    DeviceHistoryRepository deviceHistoryRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;
    WilayaRepository wilayaRepository;

    public DeviceHistoryServiceImpl(DeviceHistoryRepository deviceHistoryRepository, ModelMapper modelMapper, UserRepository userRepository, WilayaRepository wilayaRepository) {
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.wilayaRepository = wilayaRepository;
    }

    @Override
    public void addDeviceHistory(CheckOtpDto dto) {


        DeviceHistory device = modelMapper.map( dto, DeviceHistory.class );
        User user = userRepository.findByUsername( dto.getUsername() )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", dto.getUsername() ) );

        device.setAddedDate( new Date() );
        device.setNumberAttempt( 0 );
        device.setUsername( user );

        Wilaya wilaya = wilayaRepository.findByNumber( dto.getWilayaNumber() )
                .orElseThrow( () -> new ResourceNotFoundException( "Wilaya", "wilaya number", dto.getWilayaNumber() ) );

        device.setWilaya( wilaya );

        deviceHistoryRepository.save( device );
    }

    @Override
    public List<DeviceHistoryDto> getDeviceHistoryByUsername(String name) {
        List<DeviceHistory> deviceHistoryList = deviceHistoryRepository.findByUsernameUsernameAndDeletedFalse( name );

        return deviceHistoryList.stream().map( d -> modelMapper.map( d, DeviceHistoryDto.class ) ).toList();
    }

    @Override
    public void deleteDeviceHistory(String username, Long id) {
        DeviceHistory deviceHistory = deviceHistoryRepository.findByIdAndDeletedFalse( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Device History", "id", id ) );

        if( !deviceHistory.getUsername().getUsername().equals( username ) ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "This device history does not belong to current user" );
        } else {
            deviceHistory.setDeleted( true );
            deviceHistoryRepository.save( deviceHistory );
    }
    }
}
