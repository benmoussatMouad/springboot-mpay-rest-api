package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    String registerClient(RegisterDto registerDto);

    String registerMerchant(RegisterDto registerDto);
}