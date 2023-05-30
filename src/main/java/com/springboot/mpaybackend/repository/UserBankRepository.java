package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserAgency;
import com.springboot.mpaybackend.entity.UserBank;
import com.springboot.mpaybackend.entity.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBankRepository extends JpaRepository<UserBank, Long> {
    Optional<UserBank> findByUsernameUsername(String username);

    List<UserBank> findAllByBankId(Long bankId);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingAndLastNameContainingAndBankIdAndUserType(Pageable pageable, String phone, String firstName, String lastName, Long bankId, UserType userType);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingAndLastNameContainingAndBankId(Pageable pageable, String phone, String firstName, String lastName, Long bankId);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingAndLastNameContaining(Pageable pageable, String phone, String firstName, String lastName);

    Page<UserBank> findByPhoneContaining(Pageable pageable, String phone);

    Page<UserBank> findByPhoneContainingAndBankId(Pageable pageable, String phone, Long bankId);

    Page<UserBank> findByPhoneContainingAndUserType(Pageable pageable, String phone, UserType userType);

    Page<UserBank> findByFirstNameContainingAndLastNameContaining(Pageable pageable, String firstName, String lastName);

    Page<UserBank> findByFirstNameContainingAndLastNameContainingAndBankId(Pageable pageable, String firstName, String lastName, Long bankId);

    Page<UserBank> findByFirstNameContainingAndLastNameContainingAndBankIdAndUserType(Pageable pageable, String firstName, String lastName, Long bankId, UserType userType);

    Page<UserBank> findByBankId(Pageable pageable, Long bankId);

    Page<UserBank> findByBankIdAndUserType(Pageable pageable, Long bankId, UserType userType);

    Page<UserBank> findByUserType(Pageable pageable, UserType userType);

    Page<UserBank> findByPhoneContainingAndBankIdAndUserType(Pageable of, String phone, Long bankId, UserType userType);

    Page<UserBank> findByFirstNameContainingOrLastNameContainingAndBankId(Pageable of, String firstName, String lastname, Long bankId);

    Page<UserBank> findByFirstNameContainingOrLastNameContainingAndUserType(Pageable of, String firstName, String lastName, UserType userType);

    Page<UserBank> findByFirstNameContainingOrLastNameContainingAndBankIdAndUserType(Pageable of, String name, String lastName, Long bankId, UserType userType);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingOrLastNameContaining(Pageable of, String phone, String firstName, String lastName);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndBankId(Pageable of, String phone, String firstName, String lastName, Long bankId);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserType(Pageable of, String phone, String firstNale, String lastName, UserType userType);

    Page<UserBank> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndBankIdAndUserType(Pageable of, String phone, String name, String name1, Long bankId, UserType userType);

    Page<UserBank> findByFirstNameContainingOrLastNameContaining(Pageable of, String name, String name1);
}
