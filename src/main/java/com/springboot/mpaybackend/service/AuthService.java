package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.ActorLoginDto;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.PasswordChangeDto;
import com.springboot.mpaybackend.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    String registerClient(RegisterDto registerDto);

    String registerMerchant(RegisterDto registerDto);

    Boolean checkUsername(String username);

    Boolean checkPhone(String phone);

    Boolean checkClientPhone(String phone);

    Boolean checkMerchantPhone(String phone);

    Boolean verifyMerchantLogin(ActorLoginDto dto);

    Boolean verifyClientLogin(ActorLoginDto dto);

}
