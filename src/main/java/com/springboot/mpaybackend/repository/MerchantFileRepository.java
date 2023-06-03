package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantFileRepository extends JpaRepository<MerchantFile, Long> {

    List<MerchantFile> findByMerchantId(Long id);

}
