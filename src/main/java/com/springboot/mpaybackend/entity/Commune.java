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
public class Commune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commune_id")
    private Long id;

    private String name;
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;
}
