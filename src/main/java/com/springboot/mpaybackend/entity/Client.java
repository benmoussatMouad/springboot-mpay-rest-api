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
    @Column(name = "client_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User user;

    private String firstName;
    private String lastName;
    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    private String commune;
    private String postalCode;
    @Column(unique = true)
    private String phone;

    @Column(columnDefinition = "boolean DEFAULT false")
    private boolean deleted;

}
