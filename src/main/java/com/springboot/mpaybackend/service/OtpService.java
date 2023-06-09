package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.Otp;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.payload.CheckOtpDto;

public interface OtpService {

    void sendOtpToUser(Long id);

    void createOtp();

    Boolean checkOtp(CheckOtpDto dto);

    Otp createOtp(User user);
}
