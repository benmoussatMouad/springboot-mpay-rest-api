package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Wilaya;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WilayaRepository extends JpaRepository<Wilaya, Long> {
    Optional<Wilaya> findByNumber(Integer wilayaNumber);
}
