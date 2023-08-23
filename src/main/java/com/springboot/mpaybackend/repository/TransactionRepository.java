package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndDeletedFalse(Long id);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m WHERE (:id is null or t.id = :id) " +
            "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
            "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate ))" +
            "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilter(PageRequest of, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m WHERE (:id is null or t.id = :id) " +
            "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
            "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:username is null OR t.merchant.username.username = :username)" +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate ))" +
            "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndMerchant(PageRequest of, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String username);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m WHERE (:id is null or t.id = :id) " +
            "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
            "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:username is null OR t.client.user.username = :username)" +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate ))" +
            "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndClient(PageRequest of, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String username);
}
