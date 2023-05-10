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
public class UserBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private java.lang.Long userBankId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userType;

    @OneToMany
    private Set<Agency> Agency;

    @OneToOne
    private User username;

    @OneToOne
    private Bank bank;

    private Integer flag;
    private Date createdAt;
    @OneToOne
    private User createdBy;
    private Date updatedAt;
    @OneToOne
    private User updatedBy;
    private Date deletedAt;
}
