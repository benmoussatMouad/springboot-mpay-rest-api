package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.Otp;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.payload.ForgetPasswordCheckOtpDto;

public interface OtpService {

    void sendOtpToUser(Long id);

    void sendOtpToUser(String username);

    Boolean checkOtp(CheckOtpDto dto);

    Boolean checkOtp(ForgetPasswordCheckOtpDto dto, String username);

    Otp createOtp(User user);
    Otp createOtp(String username);
}
