package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.MerchantAccount;
import com.springboot.mpaybackend.payload.MerchantAccountTraceDto;

import java.util.List;

public interface MerchantAccountService {

    void createAccountForMerchantByBankCode(Long merchantId, String rib);

    void createTraceForAccount(MerchantAccount account);

    List<MerchantAccountTraceDto> getAllMerchantStatusTraces(Long merchantId);

}
