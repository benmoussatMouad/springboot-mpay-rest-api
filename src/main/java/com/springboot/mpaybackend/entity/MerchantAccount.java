package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MerchantAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private String accountNumber;
    private double balance;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;


    @Enumerated(EnumType.STRING)
    private MerchantStatus status;


}


