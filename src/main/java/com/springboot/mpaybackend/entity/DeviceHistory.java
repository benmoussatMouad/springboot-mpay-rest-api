package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceHistoryId;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User username;

    private String device;
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
    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;
    private String commune;
}
