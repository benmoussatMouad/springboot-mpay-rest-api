package com.springboot.mpaybackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private java.lang.Long wilayaId;
    @Column(unique = true)
    private Integer numWilaya;
    private String nameWilaya;

    @OneToMany(mappedBy = "wilaya")
    private Set<Client> clients;

    @OneToMany(mappedBy = "wilaya")
    private Set<Bank> banks;

    @OneToMany(mappedBy = "wilaya")
    private Set<Agency> agencies;
}
