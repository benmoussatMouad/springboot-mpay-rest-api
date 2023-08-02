package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Tm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TmRepository extends JpaRepository<Tm, Long> {

    Optional<Tm> findByBmIdAndDeletedFalse(Long id);
}
