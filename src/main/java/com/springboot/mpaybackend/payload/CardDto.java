package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class CardDto {
    private boolean enabled;
    private String cardFirst4Numbers;
    private String cardLast4Numbers;
    private Integer cardExpiryDateMonth;
    private Integer cardExpiryDateYear;
    private Long clientId;
    private String clientUserUsername;
    private Long id;
}
