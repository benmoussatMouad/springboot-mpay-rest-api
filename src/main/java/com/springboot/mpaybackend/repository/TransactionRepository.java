package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.Transaction;
import com.springboot.mpaybackend.entity.TransactionStatus;
import com.springboot.mpaybackend.entity.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'PAYMENT' AND t.status = 'CONFIRMED' AND t.transactionDate >= :lastYear")
    double calculateYearlyTurnOver(@Param("lastYear") Date date);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'PAYMENT' AND t.status = 'CONFIRMED' AND t.transactionDate >= :lastWeek AND t.client.user.username = :username")
    double calculateWeeklyTurnOverAndClient(@Param("lastWeek") Date date, String username);

    @Query("SELECT t from  Transaction t WHERE (:username is null or t.merchant.username.username = :username) " +
            "AND (:type is null OR t.type = :type) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY'))) " +
            "AND (:last4 is null OR t.pan LIKE CONCAT('%', :last4) ) " +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByFilterForMerchant(Pageable pageable, @Param("username") String username,
                                              @Param("type") TransactionType type, @Param("status") TransactionStatus status, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("last4") String last4);

    @Query("SELECT t from  Transaction t WHERE (:username is null or t.merchant.username.username = :username) " +
            "AND (t.client is not null) " +
            "AND (:type is null OR t.type = :type) " +
            "AND (:status is null OR t.status = :status) " +
            "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY')))" +
            "AND (:phone is null OR t.client.phone = :phone) " +
            "AND (:last4 is null OR t.pan LIKE CONCAT('%', :last4) )" +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByFilterForMerchantAndPhone(Pageable pageable, String username, TransactionType type, TransactionStatus status, String startDate, String endDate, String phone,
                        String last4);

    Page<Transaction> findAllByDeletedFalseAndIdAndMerchantUsernameUsernameAndOrderIdAndTerminalIdAndMerchantPhoneOrClientPhoneAndStatusAndTransactionDateAfterAndTransactionDateBeforeAndTypeAndPan(
            Pageable pageable,
            Long id,
            String merchantUsername,
            String orderId,
            String terminalId,
            String merchantPhone,
            String clientPhone,
            TransactionStatus status,
            Date startDate,
            Date endDate,
            TransactionType type,
            String pan
    );
    Optional<Transaction> findByIdAndDeletedFalse(Long id);

    @Query("SELECT t FROM Transaction t WHERE (:id is null or t.id = :id) " +
        "AND (:orderId is null OR (CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%'))) " +
        "AND (:terminalId is null OR (CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR t.merchant.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', t.pan, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:last4 is null OR t.pan LIKE CONCAT('%', :last4)) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY HH24:MI:SS') )) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilter(Pageable pageable, Long id, String orderId, String terminalId, String phone, TransactionStatus status, String startDate, String endDate, TransactionType type, String pan, String last4);

    /*@Query("SELECT t FROM Transaction t, Client c, Merchant m WHERE (:id is null or t.id = :id) " +
        "AND (:username is null OR m.username.username = :username) " +
        "AND (t.merchant.id = m.id) AND (t.client is null OR t.client.id = c.id) " + // Added space after c.id
        "AND ((:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%'))) " +
        "AND (:phone is null OR c.phone LIKE CONCAT('%', :phone, '%') OR m.phone LIKE CONCAT('%', :phone, '%') ) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + // Added space after t.type
        "AND (:pan is null OR CONCAT('%', t.pan, '%') LIKE CONCAT('%', :pan, '%')) " +
        "AND (:pan is not null OR :last4 is null OR CONCAT('%', t.pan, '%') LIKE CONCAT('%', :last4)) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY'))) " +
        "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndMerchant(Pageable pageable,
    @Param("id") Long id,
    @Param("orderId") String orderId,
    @Param("terminalId") String terminalId,
    @Param("phone") String phone,
    @Param("status")TransactionStatus status,
    @Param("startDate")String startDate,
    @Param("endDate")String endDate,
    @Param("username")String username,
    @Param("type")TransactionType type,
    @Param("pan")String pan,
    @Param("last4")String last4);*/

    @Query("SELECT t FROM Transaction t WHERE (:id is null or t.id = :id) " +
//                  "AND (:username is null OR t.merchant.username.username = :username) " +
                  "AND (:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) " +
                  "AND (:terminalId is null OR CONCAT('%', t.terminalId, '%') LIKE CONCAT('%', :terminalId, '%')) " +
                  "AND (:phone is null OR t.client.phone LIKE CONCAT('%', :phone, '%') OR t.merchant.phone LIKE CONCAT('%', :phone, '%') ) " +
                  "AND (:status is null OR t.status = :status) " +
                  "AND (:type is null OR t.type = :type) " + // Added space after t.type
                  "AND (:pan is null OR CONCAT('%', t.pan, '%') LIKE CONCAT('%', :pan, '%')) " +
                  "AND (:last4 is null OR CONCAT('%', t.pan, '%') LIKE CONCAT('%', :last4)) " +
                  "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY'))) " +
                  "AND (t.deleted = FALSE)")
    Page<Transaction> findByFilterAndMerchant(Pageable pageable,
                                              @Param("id") Long id,
                                              @Param("orderId") String orderId,
                                              @Param("terminalId") String terminalId,
                                              @Param("phone") String phone,
                                              @Param("status")TransactionStatus status,
                                              @Param("startDate")String startDate,
                                              @Param("endDate")String endDate,
//                                              @Param("username")String username,
                                              @Param("type")TransactionType type,
                                              @Param("pan")String pan,
                                              @Param("last4")String last4);


    @Query("SELECT t FROM Transaction t WHERE (:id is null or t.id = :id) " +
        "AND (:username is null OR t.client.user.username = :username) " +// Added space after c.id
        "AND (:orderId is null OR CONCAT('%', t.orderId, '%') LIKE CONCAT('%', :orderId, '%')) " +
        "AND (:phone is null OR t.client.phone LIKE CONCAT('%', :phone, '%')) " +
        "AND (:status is null OR t.status = :status) " +
        "AND (:type is null OR t.type = :type) " + 
        "AND (:last4 is null OR t.pan LIKE CONCAT('%', :last4)) " +
        "AND (:startDate is null OR :endDate is null OR (t.transactionDate >= TO_TIMESTAMP(:startDate, 'DD-MM-YYYY') AND t.transactionDate <= TO_TIMESTAMP(:endDate, 'DD-MM-YYYY'))) " +
        "AND (t.deleted = FALSE) " +
        "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByFilterAndClient(Pageable pageable, 
    Long id, 
    String orderId, 
    String phone, 
    TransactionStatus status, 
    String startDate, String endDate, 
    String username, 
    TransactionType type, 
    String last4);

    Long countByDeletedFalse();

    Long countByStatusAndDeletedFalse(TransactionStatus status);

    Long countByTypeAndDeletedFalse(TransactionType type);

    Long countByStatusAndTypeAndDeletedFalse(TransactionStatus status, TransactionType type);
    Long countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfter(TransactionStatus status, TransactionType type, Date endDate, Date startDate);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.merchant.bank = :bank AND t.deleted = false ")
    Long countByDeletedFalseForBank(Bank bank);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
            "t.merchant.bank = :bank " +
            "AND t.status = :status " +
            "AND t.deleted = false ")
    Long countByStatusAndDeletedFalseForBank(TransactionStatus status, Bank bank);


    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
            "t.merchant.bank = :bank " +
            "AND t.status = :status " +
            "AND t.type = :type " +
            "AND t.deleted = false ")
        Long countByStatusAndTypeAndDeletedFalseForBank(TransactionStatus status, TransactionType type, Bank bank);


        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'PAYMENT' " +
                "AND t.status = 'CONFIRMED' " +
                "AND t.transactionDate >= :lastYear " +
                "AND t.merchant.bank = :bank")
        double calculateYearlyTurnOverForBank(@Param("lastYear") Date prevYearTime, Bank bank);

        long countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchantBank(TransactionStatus transactionStatus, TransactionType transactionType, Date endRange, Date beginRange, Bank bank);

        long countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndClient(
                TransactionStatus confirmed, TransactionType payment, Date endRange, Date beginRange, Client client);

        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'PAYMENT' AND t.status = 'CONFIRMED' AND t.transactionDate >= :lastWeek AND t.merchant.username.username = :username")
        double calculateWeeklyTurnOverAndMerchant(Date lastWeek, String username);

        long countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchant(
                TransactionStatus confirmed, TransactionType payment, Date endRange, Date beginRange, Merchant merchant);

        List<Transaction> findAllByOrderId(String orderId);

        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
                "t.client = :client AND "+
                "t.status = :status AND "+
                "t.type = :type AND " +
                "t.transactionDate >= :beginRange AND "+
                "t.transactionDate <= :endRange")
        double sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndClient(
                TransactionStatus status, TransactionType type, Date endRange, Date beginRange, Client client);

        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
        "t.merchant = :merchant AND "+
        "t.status = :status AND "+
        "t.type = :type AND " +
        "t.transactionDate >= :beginRange AND "+
        "t.transactionDate <= :endRange")
        double sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchant(
                        TransactionStatus status, TransactionType type, Date endRange, Date beginRange,
                        Merchant merchant);

}
