package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.UserBankDto;
import com.springboot.mpaybackend.payload.UserBankPageDto;
import com.springboot.mpaybackend.payload.UserBankResponseDto;

import java.util.List;

public interface UserBankService {

    UserBankResponseDto getUserBank(Long id);

    UserBankResponseDto getUserBankByUsername(String username);

    List<UserBankDto> getUsersBankByBankId(Long bank);

    List<UserBankDto> getAllUserBanks();

    UserBankPageDto getAllUserBank(int page, int size);

    UserBankDto addUserBank(UserBankDto dto, String username);

    UserBankDto updateUserBank(UserBankDto dto, Long id, String updatingUsername);

    void deleteUserBank(Long id);

    UserBankPageDto getAllUserBankByFilter(Long id, Integer page, Integer size, String name, String phone, String userType, Long bankId);
}
