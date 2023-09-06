package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private java.lang.Long id;

    private String name;
    private String acronymName;
    private String bankCode;
    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    private String commune;
    private String phone;
    private String fax;
    private Integer totalLicence;
    private Integer totalConsumedLicence;
    private Date deleteDate;
    private Integer flag;
    @Column(columnDefinition = "boolean DEFAULT false")
    private boolean bmtmCentralized;
    @Column(columnDefinition = "boolean DEFAULT false")
    private boolean validationCentralized;
    private String bankBin;

    @OneToMany(mappedBy = "bank")
    private Set<Agency> agencies;

}


