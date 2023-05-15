package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.UserAgency;
import com.springboot.mpaybackend.entity.UserBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBankRepository extends JpaRepository<UserBank, Long> {
    Optional<UserBank> findByUsernameUsername(String username);

    List<UserBank> findAllByBankId(Long bankId);


}
