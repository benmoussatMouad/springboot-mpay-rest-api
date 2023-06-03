package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public interface BankRepository extends JpaRepository<Bank, java.lang.Long> {

    List<Bank> findByNameContaining(String name);

    List<Bank> findByPhoneContaining(String name);

    List<Bank> findByBankCodeContaining(String name);

    List<Bank> findByAddressContaining(String address);

    List<Bank> findByWilayaId(Long id);

    List<Bank> findByTotalLicenceGreaterThanEqual(Integer min);

    List<Bank> findByTotalLicenceLessThanEqual(Integer max);

    Optional<Bank> findByBankCode(String code);
}
