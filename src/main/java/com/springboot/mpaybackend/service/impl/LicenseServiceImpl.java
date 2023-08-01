package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantLicense;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.SatimAcceptDto;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.MerchantLicenseRepository;
import com.springboot.mpaybackend.service.LicenseService;
import jakarta.transaction.Transactional;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LicenseServiceImpl implements LicenseService {
    private PasswordEncoder passwordEncoder;
    private BankRepository bankRepository;
    private MerchantLicenseRepository licenseRepository;

    public LicenseServiceImpl(PasswordEncoder passwordEncoder, BankRepository bankRepository, MerchantLicenseRepository licenseRepository) {
        this.passwordEncoder = passwordEncoder;
        this.bankRepository = bankRepository;
        this.licenseRepository = licenseRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createLicense(Merchant merchant, SatimAcceptDto dto) {

        MerchantLicense license = new MerchantLicense();
        license.setMerchant(merchant);
        license.setUsername(dto.getUsername());
        license.setPassword(passwordEncoder.encode(dto.getPassword()));
        license.setTerminalId(dto.getTerminalId());

        Bank bank = merchant.getBank();

        if (bank.getTotalLicence() < bank.getTotalConsumedLicence() + 1) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Total number of licences is exceeded");
        }

        bank.setTotalConsumedLicence(bank.getTotalConsumedLicence() + 1);

        bankRepository.save(bank);

        licenseRepository.save(license);
    }
}
