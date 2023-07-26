package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserUsernameAndDeletedFalse(String username);

    Optional<Client> findByPhone(String phone);

    Boolean existsByPhoneAndDeletedFalse(String phone);

    Boolean existsByUserUsernameAndDeletedFalse(String usernameOrEmail);
}
