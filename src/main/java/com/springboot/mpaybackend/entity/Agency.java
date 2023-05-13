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
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private java.lang.Long id;
    private String agencyName;
    private String agencyCode;
    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    private String commune;
    private String phone;
    private String fax;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private Integer flag;
    private Date deletedDate;
}
