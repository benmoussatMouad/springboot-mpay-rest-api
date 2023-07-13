package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActorLoginDto extends LoginDto {
    @NotEmpty(message = "Device is required")
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
