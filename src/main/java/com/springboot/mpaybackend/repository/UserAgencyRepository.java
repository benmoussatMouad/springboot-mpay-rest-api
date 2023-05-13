package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserAgency;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserAgencyRepository extends JpaRepository<UserAgency, Long> {

    Optional<UserAgency> findByUsernameUsername(String username);

    List<UserAgency> findAllByAgencyId(Long agencyId);
}
