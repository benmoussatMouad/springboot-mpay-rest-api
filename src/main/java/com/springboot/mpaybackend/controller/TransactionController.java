package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.TransactionStatus;
import com.springboot.mpaybackend.entity.TransactionType;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.OtpService;
import com.springboot.mpaybackend.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transaction")
public class TransactionController {
    TransactionService transactionService;
    private OtpService otpService;

    public TransactionController(TransactionService transactionService, OtpService otpService) {
        this.transactionService = transactionService;
        this.otpService = otpService;
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
        if (!transactionService.confirmCardData(dto.getPan(), dto.getCvv(), dto.getMonth(), dto.getYear(), dto.getName())) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Card data are wrong");
        }
        otpService.createOtp(dto.getUsername());
        otpService.sendOtpToUser(dto.getUsername());
        return ResponseEntity.ok("Card verified, OTP sent");
    }

    @PutMapping("/{id}/form-filled")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> formFilled(@PathVariable Long id, @RequestBody @Valid FromFilledTransactionDto dto, Authentication authentication) {

        String pan = dto.getFirst6() + "******" + dto.getLast4();
        return ResponseEntity.ok(transactionService.putToFormFilled(id, authentication.getName(), dto.getDevice(), pan));
    }

    @PutMapping("/{id}/accepted")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> accepted(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToAccepted(id, authentication.getName(), dto.getDevice()));
    }

    @PostMapping("authenticate-payment")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<String> authenticateSatimPayement(@RequestBody SatimOtpDto dto) {
        otpService.satimCheckOtp(dto);
        return ResponseEntity.ok( "Card authenticated" );
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

    @PutMapping("/{id}/canceled/before")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> cancelTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToCanceledBefore(id, authentication.getName(), dto.getDevice()));
    }

    @PutMapping("/{id}/canceled/after-authentication")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'MERCHANT')")
    public ResponseEntity<TransactionDto> cancelTransactionBeforeConfirmation(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToCanceledBeforeConfirmation(id, authentication.getName(), dto.getDevice()));
    }

    @PutMapping("/{id}/canceled/after-confirmation")
    @PreAuthorize("hasAnyAuthority('MERCHANT')")
    public ResponseEntity<TransactionDto> cancelTransactionAfterConfirmation(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToCanceledAfterConfirmation(id, authentication.getName(), dto.getDevice()));
    }


    @PostMapping("cancel")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'MERCHANT')")
    public ResponseEntity<String> satimCancel() {
        // TODO
        return ResponseEntity.ok( "Transaction cancelled" );
    }

    /*@PutMapping("/{id}/canceled-by-client")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> clientCancelTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok( transactionService.putToCanceledByClient( id, authentication.getName(), dto.getDevice() ) );
    }*/

    @PutMapping("/{id}/abandoned")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<TransactionDto> abandonTransaction(@PathVariable Long id, @RequestBody @Valid SaveTransactionDto dto, Authentication authentication) {

        return ResponseEntity.ok(transactionService.putToAbandoned(id, authentication.getName(), dto.getDevice()));
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT','CLIENT','BANK_USER','BANK_ADMIN','AGENCY_USER','AGENCY_ADMIN')")
    public ResponseEntity<TransactionPage> getTransactionsByPage(
            Authentication authentication,
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String terminalId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) String last4,
            @RequestParam(required = false)
            @Parameter(description = "Value should be a string like 'DD-MM-YYYY'") String startDate,
            @RequestParam(required = false)
            @Parameter(description = "Value should be a string like 'DD-MM-YYYY'") String endDate
    ) {

        try {
            if (status != null)
                TransactionStatus.valueOf( status );
        } catch (Exception e) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Status is incorrect" );
        }

        try {
            if (type != null)
                TransactionType.valueOf( type );
        } catch (Exception e) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Type is incorrect" );
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.ok(transactionService.getTransactions(page, size,
                    id, orderId, terminalId, phone, status, startDate, endDate, type, pan, last4));
        } else if (
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("BANK_ADMIN"))
                        || authentication.getAuthorities().contains(new SimpleGrantedAuthority("BANK_USER"))
                        || authentication.getAuthorities().contains(new SimpleGrantedAuthority("AGENCY_ADMIN"))
                        || authentication.getAuthorities().contains(new SimpleGrantedAuthority("AGENCY_USER"))) {
            return ResponseEntity.ok(transactionService.getTransactions(page, size,
                    id, orderId, terminalId, phone, status, startDate, endDate, type, pan, last4));
//            transactionService.getTransactionsForBankUser(authentication.getName(), page, size,
//                    id, orderId, terminalId,phone,status, startDate, endDate);
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("MERCHANT"))) {
            return ResponseEntity.ok(transactionService.getTransactionsForMerchant(authentication.getName(), page, size,
                    id, orderId, terminalId, phone, status, startDate, endDate, type, pan, last4));
        } else {
            return ResponseEntity.ok(transactionService.getTransactionsForClient(authentication.getName(), page, size,
                    id, orderId, terminalId, phone, status, startDate, endDate, type, pan, last4));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT','CLIENT','BANK_USER','BANK_ADMIN','AGENCY_USER','AGENCY_ADMIN')")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {

        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAnyAuthority('ADMIN','MERCHANT','CLIENT','BANK_USER','BANK_ADMIN','AGENCY_USER','AGENCY_ADMIN')")
    public ResponseEntity<List<TransactionTraceDto>> getTransactionTrace(@PathVariable Long id) {

        return ResponseEntity.ok(transactionService.getTransactionTimelineById(id));
    }


}
