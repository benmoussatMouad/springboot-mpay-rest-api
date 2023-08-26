package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.TransactionTrace;
import com.springboot.mpaybackend.payload.TransactionTraceDto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTraceRepository extends JpaRepository<TransactionTrace, Long> {

    List<TransactionTraceDto> findByIdOrderByUpdatedAt(Long id);
}
