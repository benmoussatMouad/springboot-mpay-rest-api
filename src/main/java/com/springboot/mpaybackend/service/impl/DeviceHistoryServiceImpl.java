package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.DeviceHistory;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.repository.DeviceHistoryRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.DeviceHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Date;

@Service
public class DeviceHistoryServiceImpl implements DeviceHistoryService {

    DeviceHistoryRepository deviceHistoryRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;

    public DeviceHistoryServiceImpl(DeviceHistoryRepository deviceHistoryRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void addDeviceHistory(CheckOtpDto dto) {
        DeviceHistory device = modelMapper.map( dto, DeviceHistory.class );
        User user = userRepository.findByUsername( dto.getUsername() )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", dto.getUsername() ) );

        device.setAddedDate( new Date() );
        device.setNumberAttempt( 0 );
        device.setUsername( user );

        deviceHistoryRepository.save( device );
    }
}
