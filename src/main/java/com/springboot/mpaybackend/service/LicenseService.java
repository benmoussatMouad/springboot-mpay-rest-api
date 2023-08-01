package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.payload.SatimAcceptDto;

public interface LicenseService {
    void createLicense(Merchant merchant, SatimAcceptDto dto);
}
