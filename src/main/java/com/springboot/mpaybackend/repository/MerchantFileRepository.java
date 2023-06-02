package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantFileRepository extends JpaRepository<MerchantFile, Long> {
}
