package com.springboot.mpaybackend.service;

import java.util.List;

import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.payload.TransactionPage;
import com.springboot.mpaybackend.payload.TransactionTraceDto;

public interface TransactionService {
    OrderDto initOrder(double amount, Long merchantId);

    TransactionDto setOrderToWaiting(Long merchantId, double amount, String orderId, String device);

    boolean confirmCardData(String pan, String cvv, Integer month, Integer year, String name);

    TransactionDto putToFormFilled(Long id, String name, String device, String pan);

    TransactionDto putToAuthenticated(Long id, String name, String device);

    TransactionDto putToAccepted(Long id, String name, String device);

    TransactionDto putToConfirmed(Long id, String name, String device);

    TransactionDto putToRefund(Long id, String name, String device, Double amount);

    TransactionDto putToCanceledBefore(Long id, String name, String device);

    TransactionPage getTransactions(Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4);


    TransactionPage getTransactionsForMerchant(String name, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4);

    TransactionPage getTransactionsForClient(String name, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4);

    TransactionDto putToAbandoned(Long id, String name, String device);

    TransactionDto putToCanceledByClient(Long id, String name, String device);

    TransactionDto getTransactionById(Long id);

    List<TransactionTraceDto> getTransactionTimelineById(Long id);

    TransactionDto putToCanceledBeforeConfirmation(Long id, String name, String device);

    TransactionDto putToCanceledAfterConfirmation(Long id, String name, String device);
}
