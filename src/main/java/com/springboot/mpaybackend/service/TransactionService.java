package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;

public interface TransactionService {
    OrderDto initOrder(double amount, Long merchantId);

    TransactionDto setOrderToWaiting(Long merchantId, double amount, String orderId, String device);

    boolean confirmCardData(String pan, String cvv, Integer month, Integer year, String name);

    TransactionDto putToFormFilled(Long id, String name, String device);

    TransactionDto putToAuthenticated(Long id, String name, String device);

    TransactionDto putToAccepted(Long id, String name, String device);

    TransactionDto putToConfirmed(Long id, String name, String device);
}
