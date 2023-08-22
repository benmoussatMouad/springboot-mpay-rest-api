package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.repository.*;
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
    private UserRepository userRepository;
    private ClientRepository clientRepository;

    public TransactionServiceImpl(MerchantRepository merchantRepository, TransactionRepository transactionRepository, DeviceHistoryRepository deviceHistoryRepository, TransactionTraceRepository transactionTraceRepository, ModelMapper modelMapper, UserRepository userRepository, ClientRepository clientRepository) {
        this.merchantRepository = merchantRepository;
        this.transactionRepository = transactionRepository;
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.transactionTraceRepository = transactionTraceRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
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
        transaction.setTransactionDate(new Date());

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

    @Override
    public boolean confirmCardData(String pan, String cvv, Integer month, Integer year, String name) {
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TransactionDto putToFormFilled(Long id, String name, String device) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Client client = clientRepository.findByUserUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Client", " username", name));

        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(client.getUser().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.ACCEPTED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING");
        }
        transaction.setStatus(TransactionStatus.FORM_FILLED);
        transaction.setClient(client);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.FORM_FILLED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToAuthenticated(Long id, String name, String device) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Client client = clientRepository.findByUserUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Client", " username", name));

        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(client.getUser().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.FORM_FILLED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING");
        }
        transaction.setStatus(TransactionStatus.AUTHENTICATED);
        transaction.setClient(client);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.AUTHENTICATED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToAccepted(Long id, String name, String device) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Client client = clientRepository.findByUserUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Client", " username", name));

        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(client.getUser().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.WAITING)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING");
        }
        transaction.setStatus(TransactionStatus.ACCEPTED);
        transaction.setClient(client);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.ACCEPTED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToConfirmed(Long id, String name, String device) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", " username", name));

        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(merchant.getUsername().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.AUTHENTICATED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING");
        }
        transaction.setStatus(TransactionStatus.CONFIRMED);
        transaction.setMerchant(merchant);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.CONFIRMED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToRefund(Long id, String name, String device, Double refundAmount) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", " username", name));

        if (transaction.getAmount() < refundAmount) {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Refund amount is greater than transaction amount");
        }

        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(merchant.getUsername().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.CONFIRMED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING");
        }
        transaction.setStatus(TransactionStatus.REFUND);
        transaction.setAmountRefund(refundAmount);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.REFUND);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToCanceled(Long id, String name, String device) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tranasction", "id", id));
        Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse(name)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", " username", name));


        // Find device history
        List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse(device);

        DeviceHistory exists = deviceHistory.stream().filter(
                d -> d.getUsername().getUsername().equals(merchant.getUsername().getUsername())
        ).findAny().orElse(null);
        if (exists == null) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to merchant");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.CONFIRMED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be CONFIRMED");
        }
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.CANCELED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }
}
