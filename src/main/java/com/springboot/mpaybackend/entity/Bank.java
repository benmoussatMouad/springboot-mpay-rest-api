package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private java.lang.Long bankId;

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
    private Integer bmtmCentralized;
    private Integer validationCentralized;
    private String bankBin;

    @OneToMany(mappedBy = "bank")
    private Set<Agency> agencies;
}


