package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Wilaya;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Data
public class CheckOtpDto {
    private String username;
    private String otp;
    private String device;
    private String model;
    private String operatingSystem;
    private String userAgent;
    private String ipAddress;
    private Float latitude;
    private Float longitude;
    private Long wilayaId;
    private String commune;
}
