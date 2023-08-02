package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Bm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BmRepository extends JpaRepository<Bm, Long> {

    Optional<Bm> findByMerchantIdAndDeletedFalse(Long id);
}
