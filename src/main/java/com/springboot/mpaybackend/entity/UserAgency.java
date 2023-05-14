package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
public class UserAgency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_agency_id")
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User username;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    private Integer flag;
    private Date createdAt;

    @ManyToOne
    private User createdBy;

    private Date updatedAt;

    @ManyToOne
    private User updatedBy;

    private Date deletedAt;
}
