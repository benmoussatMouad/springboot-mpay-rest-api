package com.springboot.mpaybackend.utils;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.repository.BankRepository;

import static com.springboot.mpaybackend.utils.AppConstants.SIZE_OF_BANK_CODE;

public class RibProcessor {
    private static BankRepository bankRepository;

    public static Bank extractBankFrom(String rib) {
        String bankCode = rib.substring( 0, SIZE_OF_BANK_CODE );

        return bankRepository.findByBankCode( bankCode )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "Bank Code", bankCode ) );
    }

    public static void setBankRepository(BankRepository bankRepository) {
        RibProcessor.bankRepository = bankRepository;
    }
}
