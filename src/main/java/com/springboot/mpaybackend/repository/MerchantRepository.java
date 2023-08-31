package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByIdAndDeletedFalse(Long id);

    Optional<Merchant> findByUsernameUsernameAndDeletedFalse(String username);

    List<Merchant> findAllByDeletedFalse();

    List<Merchant> findAllByDeletedFalse(Pageable pageable);

    List<Merchant> findAllByDeletedFalseAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContainingAndStatusContaining(String firstName, String lastName,String phone, String registreCommerce, String fiscalNumber, String status);

    List<Merchant> findAllByDeletedFalseAndIdAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContainingAndStatusContaining(Long id, String firstName, String lastName, String phone, String registreCommerce, String fiscalNumber, String status);

    Page<Merchant> findAllByDeletedFalseAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContainingAndStatusContaining(String firstName, String lastName,String phone, String registreCommerce, String fiscalNumber, MerchantStatus status, Pageable pageable);

    Page<Merchant> findAllByDeletedFalseAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContaining(String firstName, String lastName,String phone, String registreCommerce, String fiscalNumber, Pageable pageable);

    Page<Merchant> findAllByDeletedFalseAndIdAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContainingAndStatusContaining(Long id, String firstName, String lastName, String phone, String registreCommerce, String fiscalNumber, MerchantStatus status, Pageable pageable);

    Page<Merchant> findAllByDeletedFalseAndIdAndFirstNameContainingOrLastNameContainingAndPhoneContainingAndRegistreCommerceNumberContainingAndFiscalNumberContaining(Long id, String firstName, String lastName, String phone, String registreCommerce, String fiscalNumber, Pageable pageable);

    //TODO: Add deleted false and check
    @Query("SELECT m FROM Merchant m WHERE (:id is null or m.id = :id) AND ((:firstName is null OR CONCAT('%', m.firstName, '%') LIKE CONCAT('%', :firstName, '%')) OR (:lastName is null OR CONCAT('%', m.lastName, '%') LIKE CONCAT('%', :lastName, '%'))) AND (:phone is null OR m.phone LIKE CONCAT('%', :phone, '%')) AND (:registreCommerce is null OR m.registreCommerceNumber LIKE CONCAT('%', :registreCommerce, '%')) AND (:fiscalNumber is null OR m.fiscalNumber LIKE CONCAT('%', :fiscalNumber, '%')) AND (:status is null OR m.status = :status) AND (m.deleted = FALSE)")
    Page<Merchant> findByFilter(
            Pageable pageable,
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone,
            @Param("registreCommerce") String registreCommerce,
            @Param("fiscalNumber") String fiscalNumber,
            @Param("status") MerchantStatus status
    );

    @Query("SELECT m FROM Merchant m WHERE (:id is null or m.id = :id) AND ((:firstName is null OR CONCAT('%', m.firstName, '%') LIKE CONCAT('%', :firstName, '%')) OR (:lastName is null OR CONCAT('%', m.lastName, '%') LIKE CONCAT('%', :lastName, '%'))) AND (:phone is null OR m.phone LIKE CONCAT('%', :phone, '%')) AND (:registreCommerce is null OR m.registreCommerceNumber LIKE CONCAT('%', :registreCommerce, '%')) AND (:fiscalNumber is null OR m.fiscalNumber LIKE CONCAT('%', :fiscalNumber, '%')) AND (:status is null OR m.status = :status)")
    List<Merchant> findByFilter(
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phone") String phone,
            @Param("registreCommerce") String registreCommerce,
            @Param("fiscalNumber") String fiscalNumber,
            @Param("status") MerchantStatus status
    );


    Boolean existsByPhone(String phone);

    Boolean existsByIdAndDeletedFalse(Long id);

    Boolean existsByUsernameUsername(String username);

    Long countByStatusAndDeletedFalse(MerchantStatus status);

    Long countByStatusAndDeletedFalseAndBank(MerchantStatus merchantStatus, Bank bank);
}
