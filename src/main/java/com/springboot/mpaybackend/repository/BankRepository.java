package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, java.lang.Long> {

}
