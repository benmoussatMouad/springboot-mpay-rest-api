package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndDeletedFalse(Long id);

}
