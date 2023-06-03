package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class MerchantFileResponseDto {
    private Long id;
    private Long merchantId;
    private String name;
    private String piece;
    private String content;
}
