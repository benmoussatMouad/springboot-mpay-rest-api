package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantAccountRepository extends JpaRepository<MerchantAccount, Long> {

    Optional<MerchantAccount> findByMerchantId(Long id);

    Boolean existsByMerchantIdAndMerchantDeletedFalse(Long id);
}
