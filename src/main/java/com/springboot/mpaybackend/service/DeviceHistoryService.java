package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.CheckOtpDto;

public interface DeviceHistoryService {

    void addDeviceHistory(CheckOtpDto dto);
}
