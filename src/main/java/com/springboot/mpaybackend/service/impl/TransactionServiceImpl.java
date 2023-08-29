package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.payload.TransactionPage;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.service.TransactionService;
import com.springboot.mpaybackend.payload.TransactionTraceDto;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        dto.setOrderId(generaetRandomString() );
        return dto;
    }

    private String generaetRandomString() {
 
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
    
        return generatedString;
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
        transaction.setType( TransactionType.PAYMENT );
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
        trace.setType( TransactionType.PAYMENT );
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
        trace.setType( TransactionType.PAYMENT );
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
        trace.setType( TransactionType.PAYMENT );
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
        trace.setType( TransactionType.PAYMENT );
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
        trace.setType( TransactionType.PAYMENT );
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
        // create new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setAmountRefund(refundAmount);
        newTransaction.setMerchant(merchant);
        newTransaction.setTransactionDate(new Date());
        newTransaction.setType(TransactionType.REFUND);
        newTransaction.setStatus(TransactionStatus.REFUND);

        transaction.setAmountRefund(refundAmount);
        transactionRepository.save(transaction);
        transactionRepository.save(newTransaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.REFUND);
        trace.setType( TransactionType.REFUND );
        trace.setClientDeviceHistory(exists);

        TransactionTrace newTrace = new TransactionTrace();
        newTrace.setTransaction(newTransaction);
        newTrace.setUpdatedAt(new Date());
        newTrace.setStatus(TransactionStatus.REFUND);
        newTrace.setType( TransactionType.REFUND );
        newTrace.setMerchantDeviceHistory(exists);

        transactionTraceRepository.save(trace);
        transactionTraceRepository.save(newTrace);

        return modelMapper.map(newTransaction, TransactionDto.class);
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
        if (!transaction.getStatus().equals(TransactionStatus.CONFIRMED) && 
        !transaction.getStatus().equals(TransactionStatus.WAITING) &&
        !transaction.getStatus().equals(TransactionStatus.ACCEPTED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be CONFIRMED or WAITING or ACCEPTED");
        }
        // create new transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setMerchant(merchant);
        newTransaction.setTransactionDate(new Date());
        newTransaction.setType(TransactionType.CANCELLATION);
        newTransaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(newTransaction);
        
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.CANCELED);
        trace.setType( TransactionType.CANCELLATION );
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(newTransaction, TransactionDto.class);
    }

    @Override
    public TransactionPage getTransactions(Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4) {
        Page<Transaction> transactionPage = transactionRepository.findByFilter(
                PageRequest.of(page, size),
                id, orderId, terminalId, phone, (status!=null? TransactionStatus.valueOf(status): null), startDate, endDate,
                (type!=null? TransactionType.valueOf(type) : null), pan, last4
        );

        TransactionPage dto = new TransactionPage();

        dto.setCount(transactionPage.getTotalElements());
        dto.setPage(transactionPage.getContent().stream().map(
                t ->{ return  modelMapper.map(t, TransactionDto.class);}
        ).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public TransactionPage getTransactionsForMerchant(String username, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4) {

        Page<Transaction> transactionPage = transactionRepository.findByFilterAndMerchant(
                PageRequest.of(page, size),
                id, orderId, terminalId, phone, (status!=null? TransactionStatus.valueOf(status): null), startDate, endDate, username,
                (type!=null? TransactionType.valueOf(type) : null), pan, last4
        );

        TransactionPage dto = new TransactionPage();

        dto.setCount(transactionPage.getTotalElements());
        dto.setPage(transactionPage.getContent().stream().map(
                t -> modelMapper.map(t, TransactionDto.class)
        ).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public TransactionPage getTransactionsForClient(String username, Integer page, Integer size, Long id, String orderId, String terminalId, String phone, String status, String startDate, String endDate, String type, String pan, String last4) {
        Page<Transaction> transactionPage = transactionRepository.findByFilterAndClient(
                PageRequest.of(page, size),
                id, orderId, terminalId, phone, (status!=null? TransactionStatus.valueOf(status): null), startDate, endDate, username,
                (type!=null? TransactionType.valueOf(type) : null), pan, last4
        );

        TransactionPage dto = new TransactionPage();

        dto.setCount(transactionPage.getTotalElements());
        dto.setPage(transactionPage.getContent().stream().map(
                t -> modelMapper.map(t, TransactionDto.class)
        ).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public TransactionDto putToAbandoned(Long id, String name, String device) {
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
        if (!transaction.getStatus().equals(TransactionStatus.WAITING) && !transaction.getStatus().
        equals(TransactionStatus.FORM_FILLED)) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be WAITING or FORM_FILLED");
        }
        transaction.setStatus(TransactionStatus.ABANDONED);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.ABANDONED);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto putToCanceledByClient(Long id, String name, String device) {
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
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device does not belong to client");
        }

        // Set Transaction status but confirm previous status
        if (!transaction.getStatus().equals(TransactionStatus.ACCEPTED) &&
            !transaction.getStatus().equals( TransactionStatus.FORM_FILLED )&&
                !transaction.getStatus().equals( TransactionStatus.AUTHENTICATED )) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Transaction previous status must be AUTHENTICATED or FORM_FILLED or ACCEPTED");
        }
        transaction.setStatus(TransactionStatus.CANCELED_BY_CLIENT);
        transactionRepository.save(transaction);
        // Save trace
        TransactionTrace trace = new TransactionTrace();
        trace.setTransaction(transaction);
        trace.setUpdatedAt(new Date());
        trace.setStatus(TransactionStatus.CANCELED_BY_CLIENT);
        trace.setClientDeviceHistory(exists);
        transactionTraceRepository.save(trace);

        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        
        return modelMapper.map(transaction, TransactionDto.class);        
    }

    @Override
    public List<TransactionTraceDto> getTransactionTimelineById(Long id) {
        List<TransactionTrace> list = transactionTraceRepository.findByIdOrderByUpdatedAt(id);
        
        return list.stream().map(trace -> modelMapper.map(trace, TransactionTraceDto.class)).toList();
    }
}
