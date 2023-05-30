package com.springboot.mpaybackend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClientAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String cardNumber;
    private Date cardExpiryDate;

    private double minCredit;

    // TODO: Make Client status
    private String status;
}
