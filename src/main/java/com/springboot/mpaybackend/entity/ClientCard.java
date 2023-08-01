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
public class ClientCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String cardFirst6Numbers;
    private String cardLast4Numbers;
    private Integer cardExpiryDateMonth;
    private Integer cardExpiryDateYear;

    private double minCredit;

    // TODO: Make Client status
    private boolean enabled;

    @Column(columnDefinition = "boolean DEFAULT false")
    private boolean deleted;

}
