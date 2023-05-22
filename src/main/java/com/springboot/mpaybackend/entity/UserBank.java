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
@Entity(name= "user_bank")
public class UserBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_bank_id")
    private java.lang.Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User username;

    @ManyToOne
    @JoinColumn(name = "bank_id")
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
