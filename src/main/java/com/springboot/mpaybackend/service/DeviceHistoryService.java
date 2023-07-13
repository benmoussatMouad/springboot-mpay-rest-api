package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.payload.DeviceHistoryDto;

import java.util.List;

public interface DeviceHistoryService {

    void addDeviceHistory(CheckOtpDto dto);

    List<DeviceHistoryDto> getDeviceHistoryByUsername(String name);

    void deleteDeviceHistory(String username, Long id);
}
