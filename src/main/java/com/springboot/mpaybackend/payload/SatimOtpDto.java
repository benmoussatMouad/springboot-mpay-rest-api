package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class SatimOtpDto {
    private String username;
    private String otp;
//    private String device;
}
