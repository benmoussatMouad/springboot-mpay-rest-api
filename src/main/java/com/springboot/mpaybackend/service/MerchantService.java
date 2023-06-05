package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.MerchantBankInfoDto;
import com.springboot.mpaybackend.payload.MerchantDto;
import com.springboot.mpaybackend.payload.MerchantPageDto;
import com.springboot.mpaybackend.payload.MerchantResponseDto;
import jakarta.annotation.Nullable;

import java.util.List;

public interface MerchantService {

    MerchantResponseDto addMerchant(MerchantDto dto, Boolean byBankUser);

    MerchantResponseDto getMerchant(Long id);

    MerchantResponseDto getMerchantByUsername(String username);

    List<MerchantResponseDto> getAllMerchants();

    List<MerchantResponseDto> getAllMerchants(int page, int size);

    MerchantPageDto getAllMerchantsByFilter(int page, int size, Long id, @Nullable String firstName, @Nullable String lastName, String phone, String registreCommerce, String numeroFiscal, String status);

    MerchantResponseDto updateMerchant(MerchantDto dto, Long id);

    void deleteMerchant(Long id);

    MerchantResponseDto fillInfo(MerchantBankInfoDto dto, Long id);

    void blockMerchantAccount(Long id);
}
