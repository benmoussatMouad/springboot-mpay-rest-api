package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.TransactionTrace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTraceRepository extends JpaRepository<TransactionTrace, Long> {
}
