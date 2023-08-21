package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;

public interface TransactionService {
    OrderDto initOrder(double amount, Long merchantId);

    TransactionDto setOrderToWaiting(Long merchantId, double amount, String orderId, String device);
}
