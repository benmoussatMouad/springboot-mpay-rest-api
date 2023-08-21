package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class CardDataDto {
    private String username;
    private String pan;
    private String cvv;
    private Integer month;
    private Integer year;
    private String name;
}
