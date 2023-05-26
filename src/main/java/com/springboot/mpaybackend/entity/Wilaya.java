package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Wilaya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wilaya_id")
    private java.lang.Long id;

    @Column(unique = true)
    private Integer number;
    private String name;

    @OneToMany(mappedBy = "wilaya")
    private Set<Client> clients;

    @OneToMany(mappedBy = "wilaya")
    private Set<Bank> banks;

    @OneToMany(mappedBy = "wilaya")
    private Set<Agency> agencies;

    @OneToMany(mappedBy = "wilaya")
    private Set<DeviceHistory> deviceHistories;
}
