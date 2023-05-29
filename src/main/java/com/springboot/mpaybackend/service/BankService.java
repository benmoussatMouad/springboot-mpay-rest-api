package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.payload.BankLightDto;
import com.springboot.mpaybackend.payload.BankPageDto;

import java.util.List;

public interface BankService {

    BankDto addBank(BankDto bankDto);

    BankDto getBank(Long bankId);

    List<BankDto> getBanks();

    BankDto updateBank(BankDto bankDto, Long bankId);

    void deleteBank(Long bankId);

    List<BankLightDto> getBanksLightFormat();

    BankPageDto getBanks(int page, int size);

    List<BankDto> getBanksByNameContaining(String bankName);

    List<BankDto> getBanksByWilaya(Long wilayaId);

    List<BankDto> getBanksByCodeContaining(String bankCode);

    List<BankDto> getBanksByTotalLicenseLesserOrEqualThan(Integer maxLicense);

    List<BankDto> getBanksByTotalLicenseGreaterOrEqualThan(Integer minLicense);

    List<BankDto> getBanksByAddressContaining(String address);

    List<BankDto> getBanksByPhoneContaining(String phone);
}
