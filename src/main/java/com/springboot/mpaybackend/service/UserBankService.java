package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.payload.UserBankDto;

import java.util.List;

public interface UserBankService {

    UserBankDto getUserBank(Long id);

    UserBankDto getUserBankByUsername(String username);

    List<UserBankDto> getUsersBankByBankId(Long bank);

    UserBankDto addUserBank(UserBankDto dto, String username);

    List<UserBankDto> getAllUserBanks();

    UserBankDto updateUserBank(UserBankDto dto, Long id, String updatingUsername);

    void deleteUserBank(Long id);
}
