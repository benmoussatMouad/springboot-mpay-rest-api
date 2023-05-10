package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgencyRepository extends JpaRepository<Agency, java.lang.Long> {

    List<Agency> findByBankBankId(Long bankid);
}
