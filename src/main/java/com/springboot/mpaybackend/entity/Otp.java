package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private DeviceHistory device;

    private String code;
    private Integer attempts;
    private Date createdAt;
    private boolean expired;
    private boolean used;
}
