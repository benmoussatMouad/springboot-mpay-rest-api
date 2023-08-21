package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class OrderDto {
    private String orderId;
    private Long merchantId;
    private String merchantName;
    private double amount;
}
