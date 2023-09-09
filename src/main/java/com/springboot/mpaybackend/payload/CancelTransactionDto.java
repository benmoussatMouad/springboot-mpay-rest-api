package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelTransactionDto {
    private Double amount;
    private Long merchantId;
    private String orderId;
    @NotEmpty
    private String device;
    @NotEmpty
    private String password;
}
