package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long id;
    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User username;


    private String firstName;
    private String lastName;
    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    private String commune;
    private String postalCode;

    @Column(unique = true)
    private String phone;

    @OneToOne
    @JoinColumn(name = "bm_id")
    private Bm bm;

    private String identityCardNumber;
    private String fiscalNumber;
    private String registreCommerceNumber;
    private String articleImpotsNumber;
    private Integer numberCheckoutRequested;


    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'NON_VERIFIED'")
    private MerchantStatus status;

    @Column(columnDefinition = "boolean default false")
    private boolean enabled;
}
