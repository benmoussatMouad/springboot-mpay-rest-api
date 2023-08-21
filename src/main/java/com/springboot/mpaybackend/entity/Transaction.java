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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private double amount;
    private double amountRefund;

    private String type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Date transactionDate;
    private String orderId;

    private String satimId;
    @Column(columnDefinition = "boolean DEFAULT false")
    private boolean deleted;
}
