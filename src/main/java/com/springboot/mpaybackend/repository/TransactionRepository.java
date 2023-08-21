package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
