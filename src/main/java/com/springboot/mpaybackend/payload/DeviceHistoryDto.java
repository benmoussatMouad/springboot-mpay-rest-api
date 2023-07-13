package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Wilaya;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Data
public class DeviceHistoryDto {
    private Long id;
    private String model;
    private String operatingSystem;
    private String state;
    private Date addedDate;
    private String userAgent;
    private Integer numberAttempt;
    private String ipAddress;
    private String sessionState;
    private Float latitude;
    private Float longitude;
    private String wilayaName;
    private Long wilayaId;
    private String commune;
}
