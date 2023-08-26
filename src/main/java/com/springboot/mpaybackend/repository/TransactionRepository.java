package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndDeletedFalse(Long id);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
        "AND (t.merchant.id = m.id) AND t.client.id = c.id AND cc.client.id = c.id " + // Added space after c.id
        "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers) " +
        "AND (:startDate is null OR :endDate is null ) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilter(Pageable pageable, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
            "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
            "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:type is null OR t.type = :type)" +
            "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%'))" +
            "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers)" +
            "AND (:username is null OR t.merchant.username.username = :username)" +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate ))" +
            "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndMerchant(Pageable pageable, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String username, String type, String pan, String last4);

    @Query("SELECT t FROM Transaction t, Client c, Merchant m, ClientCard cc WHERE (:id is null or t.id = :id) " +
            "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
            "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:type is null OR t.type = :type)" +
            "AND (:pan is null OR CONCAT('%', cc.cardFirst6Numbers, '%') LIKE CONCAT('%', :pan, '%') OR CONCAT('%', cc.cardLast4Numbers, '%') LIKE CONCAT('%', :pan, '%'))" +
            "AND (:pan is not null OR :last4 is null OR :last4 = cc.cardLast4Numbers)" +
            "AND (:username is null OR t.client.user.username = :username)" +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= :startDate AND t.transactionDate <= :endDate ))" +
            "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndClient(Pageable pageable, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String username, String type, String pan, String last4);
}
