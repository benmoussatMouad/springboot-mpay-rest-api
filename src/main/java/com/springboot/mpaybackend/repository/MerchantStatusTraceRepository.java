package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.MerchantStatusTrace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantStatusTraceRepository extends JpaRepository<MerchantStatusTrace, Long> {

    List<MerchantStatusTrace> findAllByMerchantIdOrderByCreatedAt(Long merchantId);
}
