package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantLicenseRepository extends JpaRepository<MerchantLicense, Long> {

    List<MerchantLicense> findByMerchantId(Long id);
}
