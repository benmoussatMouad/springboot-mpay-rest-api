package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.repository.DeviceHistoryRepository;
import com.springboot.mpaybackend.repository.MerchantRepository;
import com.springboot.mpaybackend.repository.TransactionRepository;
import com.springboot.mpaybackend.repository.TransactionTraceRepository;
import com.springboot.mpaybackend.service.TransactionService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private MerchantRepository merchantRepository;
    private TransactionRepository transactionRepository;
    private DeviceHistoryRepository deviceHistoryRepository;
    private TransactionTraceRepository transactionTraceRepository;
    private ModelMapper modelMapper;

    public TransactionServiceImpl(MerchantRepository merchantRepository, TransactionRepository transactionRepository, DeviceHistoryRepository deviceHistoryRepository, TransactionTraceRepository transactionTraceRepository, ModelMapper modelMapper) {
        this.merchantRepository = merchantRepository;
        this.transactionRepository = transactionRepository;
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.transactionTraceRepository = transactionTraceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDto initOrder(double amount, Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", merchantId));

        OrderDto dto = new OrderDto();
        dto.setAmount(amount);
        dto.setMerchantId(merchant.getId());
        dto.setMerchantName(merchant.getLastName() + " " + merchant.getFirstName());
        dto.setOrderId( Double.valueOf(Math.random()*100000).toString());
        return dto;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionDto setOrderToWaiting(Long merchantId, double amount, String orderId, String device) {
        //Create new transaction
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", "id", merchantId));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.WAITING);
        transaction.setClient(null);
        transaction.setMerchant(merchant);
        transaction.setOrderId(orderId);

        Transaction savedTransaction = transactionRepository.save(transaction);
        //create new trace
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(merchant.getUsername().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }


        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setStatus(TransactionStatus.WAITING);
        trace.setUpdatedAt(new Date());
        trace.setClientDeviceHistory(null);
        trace.setMerchantDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(savedTransaction, TransactionDto.class);
    }
}
