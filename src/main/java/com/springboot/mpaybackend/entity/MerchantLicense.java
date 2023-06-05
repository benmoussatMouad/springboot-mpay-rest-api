package com.springboot.mpaybackend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MerchantLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_license_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private String terminalId;
    private String postNumber;

    private Date expiryDate;
}
