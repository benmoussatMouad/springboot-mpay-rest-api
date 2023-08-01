package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.*;
import jakarta.annotation.Nullable;

import java.util.List;

public interface MerchantService {

    MerchantResponseDto addMerchant(MerchantDto dto, Boolean byBankUser, String useraname);

    MerchantResponseDto getMerchant(Long id);

    MerchantResponseDto getMerchantByUsername(String username);

    List<MerchantResponseDto> getAllMerchants();

    List<MerchantResponseDto> getAllMerchants(int page, int size);

    MerchantPageDto getAllMerchantsByFilter(int page, int size, Long id, @Nullable String firstName, @Nullable String lastName, String phone, String registreCommerce, String numeroFiscal, String status);

    MerchantResponseDto updateMerchant(MerchantDto dto, Long id);

    void deleteMerchant(Long id);

    MerchantResponseDto fillInfo(MerchantBankInfoDto dto, Long id);

    void blockMerchantAccount(Long id, BlockRequestDto dto, String usernameOfBlocker);

    void unBlockMerchantAccount(Long id, BlockRequestDto dto, String usernameOfBlocker);

    MerchantDto putInProgress(Long id);

    MerchantDto demandReviewFile(Long id, String feedback);

    MerchantDto rejectMerchant(Long id);

    MerchantDto acceptMerchantByBank(Long id, AcceptMerchantDemandDto dto, String username);

    MerchantPageDto getAllMerchantsByFilterForSpecificBank(Integer page, Integer size, Long id, String name, String name1, String phone, String regCommerce, String nif, String status, String callingUsername);

    MerchantDto putToSatimReview(Long id, SatimReviewDto dto, String name);

    MerchantDto putToSatimAccepted(Long id, SatimAcceptDto dto, String name);

    MerchantDto putToSatimrejected(Long id, SatimAcceptDto dto, String name);

    MerchantDto verifyMerchant(Long id, String name);
}
