package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FromFilledTransactionDto {
    @NotEmpty
    private String device;

    @Size(min = 6)
    @Size(max=6)
    private String first6;
    
    @Size(min = 4)
    @Size(max = 4)
    private String last4;

    private Double amount;
    private Long merchantId;
    private String orderId;
}
