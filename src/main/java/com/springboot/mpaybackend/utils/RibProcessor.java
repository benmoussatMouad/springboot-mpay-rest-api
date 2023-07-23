package com.springboot.mpaybackend.utils;

import com.springboot.mpaybackend.entity.Agency;
import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.BankRepository;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.springboot.mpaybackend.utils.AppConstants.SIZE_OF_AGENCY_CODE;
import static com.springboot.mpaybackend.utils.AppConstants.SIZE_OF_BANK_CODE;

public class RibProcessor {
    private static BankRepository bankRepository;
    private static AgencyRepository agencyRepository;

    public static Bank extractBankFrom(String rib) {
        String bankCode = rib.substring( 0, SIZE_OF_BANK_CODE );

        // Check if agency is part of bank
        String agencyCode = rib.substring( 3, 3 + SIZE_OF_AGENCY_CODE );
        List<Agency> agencies = agencyRepository.findByAgencyCode( agencyCode );


        return bankRepository.findByBankCode( bankCode )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "Bank Code", bankCode ) );
    }

    public static Agency extractAgencyFrom(String rib) {
        String bankCode = rib.substring( 0, SIZE_OF_BANK_CODE );

        // Check if agency is part of bank
        String agencyCode = rib.substring( 3, 3 + SIZE_OF_AGENCY_CODE );
        List<Agency> agencies = agencyRepository.findByAgencyCode( agencyCode );

        Bank bank = bankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "Bank Code", bankCode));

        return agencies.stream().filter(agency -> agency.getBank().equals(bank)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Agency", "Agency Code", agencyCode));
    }

    public static void setAgencyRepository(AgencyRepository agencyRepository) {
        RibProcessor.agencyRepository = agencyRepository;
    }

    public static void setBankRepository(BankRepository bankRepository) {
        RibProcessor.bankRepository = bankRepository;
    }
}
