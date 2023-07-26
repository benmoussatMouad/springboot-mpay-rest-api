package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class AddCardDto {

    private String cardFirst4Numbers;
    private String cardLast4Numbers;
    private Integer cardExpiryDateMonth;
    private Integer cardExpiryDateYear;
}
