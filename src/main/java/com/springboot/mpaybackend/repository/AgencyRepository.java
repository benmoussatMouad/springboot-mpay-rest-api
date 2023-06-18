package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, java.lang.Long> {

    List<Agency> findByBankId(Long bankid);

    List<Agency> findByWilayaId(Long id);

    List<Agency> findByAgencyNameContaining(String name);

    List<Agency> findByAgencyCodeContaining(String code);

    List<Agency> findByPhoneContaining(String phone);

    List<Agency> findAllByCommune(String commune);

    List<Agency> findAllByAgencyCode(String agencyCode);

    Optional<Agency> findByAgencyCode(String agencyCode);

    List<Agency> findAllByPhone(String phone);
}
