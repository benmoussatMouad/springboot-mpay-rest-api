package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.OtpService;
import com.springboot.mpaybackend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {
    TransactionService transactionService;
    private OtpService otpService;

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

    @PostMapping("init-payment")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<String> initiatePayment(@RequestBody CardDataDto dto) {
        if (transactionService.confirmCardData(dto.getPan(), dto.getCvv(), dto.getMonth(), dto.getYear(), dto.getName())) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Card data are wrong");
        }
        otpService.createOtp(dto.getUsername());
        otpService.sendOtpToUser(dto.getUsername());
        return ResponseEntity.ok("Card verified, OTP sent");
    }

    @PutMapping("/{id}/form-filled")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> formFilled(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToFormFilled(id, authentication.getName(), dto.getDevice()));
    }

    @PutMapping("/{id}/accepted")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> accepted(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToAccepted(id, authentication.getName(), dto.getDevice()));
    }

    @PostMapping("authenticate-payment")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<String> authenticateSatimPayement() {
        return null;
    }

    @PutMapping("/{id}/authenticated")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> authenticated(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToAuthenticated(id, authentication.getName(), dto.getDevice()));
    }

    @PostMapping("confirm-payment")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<String> satimConfirmPayment() {
        return ResponseEntity.ok("Payment confrimed");
    }

    @PutMapping("/{id}/confirmed")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> confirmTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToConfirmed(id, authentication.getName(), dto.getDevice()));
    }

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> refundTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToRefund(id, authentication.getName(), dto.getDevice(), dto.getAmount()));
    }

    @PutMapping("/{id}/canceled")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> cancelTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToCanceled(id, authentication.getName(), dto.getDevice()));
    }


}
