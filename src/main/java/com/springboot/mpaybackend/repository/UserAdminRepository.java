package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {

    Optional<UserAdmin> findByUsernameUsername(String username);
}
