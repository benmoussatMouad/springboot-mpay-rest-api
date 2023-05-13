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

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User username;

    @OneToOne
    private Bank bank;

    private Integer flag;
    private Date createdAt;

    @ManyToOne
    private User createdBy;

    private Date updatedAt;

    @ManyToOne
    private User updatedBy;

    private Date deletedAt;
}
