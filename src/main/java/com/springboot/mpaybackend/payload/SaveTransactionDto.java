package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveTransactionDto {
    @NotNull
    private Double amount;
    @NotNull
    private Long merchantId;
    @NotNull
    private String orderId;
    @NotEmpty
    private String device;
}
