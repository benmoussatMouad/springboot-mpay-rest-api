package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class ActorLoginDto extends LoginDto {
    private String device;
    private String model;
    private String operatingSystem;
    private String userAgent;
    private String ipAddress;
    private Float latitude;
    private Float longitude;
    private String wilayaNumber;
    private String commune;
}
