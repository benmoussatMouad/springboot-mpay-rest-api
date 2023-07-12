package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Date expiresAt;
    private boolean used;

    public Boolean isExpired() {
        return expiresAt.compareTo(new Date()) <= 0;
    }

    public void increaseAttempt() {
        this.attempts++;
    }
}
