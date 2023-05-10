package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.BankDto;

import java.util.List;

public interface BankService {

    BankDto addBank(BankDto bankDto);

    BankDto getBank(Long bankId);

    List<BankDto> getBanks();

    BankDto updateBank(BankDto bankDto, Long bankId);

    void deleteBank(Long bankId);
}
