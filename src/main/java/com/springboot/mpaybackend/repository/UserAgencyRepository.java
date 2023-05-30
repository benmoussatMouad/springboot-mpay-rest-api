package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserAgency;
import com.springboot.mpaybackend.entity.UserBank;
import com.springboot.mpaybackend.entity.UserType;
import com.springboot.mpaybackend.payload.UserAgencyPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserAgencyRepository extends JpaRepository<UserAgency, Long> {

    Optional<UserAgency> findByUsernameUsername(String username);

    List<UserAgency> findAllByAgencyId(Long agencyId);

    Page<UserAgency> findByAgencyBankId(Pageable of, Long bankId);

    Page<UserAgency> findByUserType(Pageable of, UserType userType);

    Page<UserAgency> findByAgencyBankIdAndUserType(Pageable pageable, Long bankId, UserType userType);

    Page<UserAgency> findByPhoneContaining(Pageable pageable, String phone);

    Page<UserAgency> findByPhoneContainingAndAgencyBankId(Pageable of, String phone, Long bankId);

    Page<UserAgency> findByPhoneContainingAndUserType(Pageable pageable, String phone, UserType userType);

    Page<UserAgency> findByPhoneContainingAndAgencyBankIdAndUserType(Pageable pageable, String phone, Long bankId, UserType userType);

    Page<UserAgency> findByFirstNameContainingOrLastNameContaining(Pageable pageable, String f, String l);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndAgencyBankId(Pageable pageable, String f, String l, Long bankId);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndUserType(Pageable pageable, String f, String l, UserType userType);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndAgencyBankIdAndUserType(Pageable pageable, String f, String l, Long bankId, UserType userType);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContaining(Pageable pageable, String phone, String s, String s1);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyBankId(Pageable pageable, String phone, String name, String name1, Long bankId);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserType(Pageable pageable, String phone, String name, String name1, UserType userType);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyBankIdAndUserType(Pageable pageable, String phone, String name, String name1, Long bankId, UserType userType);

    Page<UserAgency> findByAgencyId(Pageable pageable, Long agencyId);

    Page<UserAgency> findByAgencyIdAndAgencyBankId(Pageable pageable, Long agencyId, Long bankId);

    Page<UserAgency> findByAgencyIdAndUserType(Pageable pageable, Long agencyId, UserType userType);

    Page<UserAgency> findByAgencyIdAndAgencyBankIdAndUserType(Pageable pageable, Long agencyId, Long bankId, UserType userType);

    Page<UserAgency> findByPhoneContainingAndAgencyId(Pageable pageable, String phone, Long agencyId);

    Page<UserAgency> findByPhoneContainingAndAgencyIdAndAgencyBankId(Pageable pageable, String phone, Long agencyId, Long bankId);

    Page<UserAgency> findByPhoneContainingAndUserTypeAndAgencyId(Pageable pageable, String phone, UserType valueOf, Long agencyId);

    Page<UserAgency> findByPhoneContainingAndAgencyIdAndAgencyBankIdAndUserType(Pageable pageable, String phone, Long agencyId, Long bankId, UserType userType);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndAgencyId(Pageable pageable, String name, String name1, Long agencyId);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankId(Pageable pageable, String name, String name1,Long agencyId, Long bankId);

    Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndUserTypeAndAgencyId(Pageable pageable, String name, String name1, UserType userType, Long agencyId);

Page<UserAgency> findByFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankIdAndUserType(Pageable pageable, String name, String name1, Long agencyId, Long bankId, UserType valueOf);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyId(Pageable pageable, String phone, String name, String name1, Long agencyId);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankId(Pageable pageable, String phone, String name, String name1, Long agencyId, Long bankId);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndUserTypeAndAgencyId(Pageable pageable, String phone, String name, String name1, UserType userType, Long agencyId);

    Page<UserAgency> findByPhoneContainingAndFirstNameContainingOrLastNameContainingAndAgencyIdAndAgencyBankIdAndUserType(Pageable pageable, String phone, String name, String name1, Long agencyId, Long bankId, UserType userType);
}
