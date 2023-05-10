package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Otp;
import com.springboot.mpaybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByUser(User user);

    Boolean existsByUser(User user);
}
