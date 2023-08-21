package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.OrderDto;
import com.springboot.mpaybackend.payload.OrderRequestDto;
import com.springboot.mpaybackend.payload.SaveTransactionDto;
import com.springboot.mpaybackend.payload.TransactionDto;
import com.springboot.mpaybackend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {
    TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/init")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<OrderDto> initTransaction(@RequestBody OrderRequestDto dto) {

        
        return ResponseEntity.ok(transactionService.initOrder(dto.getAmount(), dto.getMerchantId()));
    }

    @PutMapping("init")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> putTransactionToInit(@RequestBody @Valid SaveTransactionDto dto) {

        return ResponseEntity.ok(transactionService.setOrderToWaiting(dto.getMerchantId(), dto.getAmount(), dto.getOrderId(), dto.getDevice()));
    }
}
