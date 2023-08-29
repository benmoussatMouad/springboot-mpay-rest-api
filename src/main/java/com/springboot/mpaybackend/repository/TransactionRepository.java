package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Transaction;
import com.springboot.mpaybackend.entity.TransactionStatus;
import com.springboot.mpaybackend.entity.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndDeletedFalse(Long id);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
        "AND (t.merchant.id = m.id) AND (t.client is null OR t.client.id = c.id) AND (t.client is null OR cc.client.id = c.id) " + // Added space after c.id
        "AND (:orderId is null OR (CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%'))) " +
        "AND (:terminalId is null OR (CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY'))) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilter(Pageable pageable, Long id, String orderId, String terminalId, String phone, TransactionStatus status, String startDate, String endDate, TransactionType type, String pan, String last4);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
        "AND (:username is null OR m.username.username = :username) " +
        "AND (t.merchant.id = m.id) AND (t.client is null OR t.client.id = c.id) AND (t.client is null OR cc.client.id = c.id) " + // Added space after c.id
        "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate )) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndMerchant(Pageable pageable, Long id, String orderId, String terminalId, String phone, TransactionStatus status, String startDate, String endDate, String username, TransactionType type, String pan, String last4);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
        "AND (:username is null OR c.user.username = :username) " +
        "AND (t.merchant.id = m.id) AND (t.client is null OR t.client.id = c.id) AND (t.client is null OR cc.client.id = c.id) " + // Added space after c.id
        "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate )) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndClient(Pageable pageable, Long id, String orderId, String terminalId, String phone, TransactionStatus status, String startDate, String endDate, String username, TransactionType type, String pan, String last4);
}
