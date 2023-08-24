package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.payload.TransactionPage;

import java.util.Date;

public interface TransactionService {
    OrderDto initOrder(double amount, Long merchantId);

    TransactionDto setOrderToWaiting(Long merchantId, double amount, String orderId, String device);

    boolean confirmCardData(String pan, String cvv, Integer month, Integer year, String name);

    TransactionDto putToFormFilled(Long id, String name, String device);

    TransactionDto putToAuthenticated(Long id, String name, String device);

    TransactionDto putToAccepted(Long id, String name, String device);

    TransactionDto putToConfirmed(Long id, String name, String device);

    TransactionDto putToRefund(Long id, String name, String device, Double amount);

    TransactionDto putToCanceled(Long id, String name, String device);

    TransactionPage getTransactions(Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate);


    TransactionPage getTransactionsForMerchant(String name, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate);

    TransactionPage getTransactionsForClient(String name, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate);

    TransactionDto putToAbandoned(Long id, String name, String device);

    TransactionDto putToCanceledByClient(Long id, String name, String device);
}
