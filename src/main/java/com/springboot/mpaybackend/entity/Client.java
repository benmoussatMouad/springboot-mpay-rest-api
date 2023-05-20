package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User username;

    private String firstName;
    private String lastName;
    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    private String commune;
    private String postalCode;
    private String phone;

    //TODO: add account for client

}
