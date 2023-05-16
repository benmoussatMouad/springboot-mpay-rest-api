package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.payload.UserBankDto;
import com.springboot.mpaybackend.payload.UserBankPageDto;

import java.util.List;

public interface UserBankService {

    UserBankDto getUserBank(Long id);

    UserBankDto getUserBankByUsername(String username);

    List<UserBankDto> getUsersBankByBankId(Long bank);

    List<UserBankDto> getAllUserBanks();

    UserBankPageDto getAllUserBank(int page, int size);

    UserBankDto addUserBank(UserBankDto dto, String username);

    UserBankDto updateUserBank(UserBankDto dto, Long id, String updatingUsername);

    void deleteUserBank(Long id);
}
