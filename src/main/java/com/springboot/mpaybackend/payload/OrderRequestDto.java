package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class OrderRequestDto {
    private double amount;
    private Long merchantId;
}
