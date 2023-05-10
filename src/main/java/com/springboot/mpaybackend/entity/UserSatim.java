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
public class UserSatim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSatimId;
    private String firstName;
    private String lastName;
    private String phone;
    private String user_type;
    @OneToOne
    @JoinColumn(referencedColumnName = "username")
    private User username;
    @OneToOne
    @JoinColumn(referencedColumnName = "username")
    private User createdBy;
    @OneToOne
    @JoinColumn(referencedColumnName = "username")
    private User updatedBy;
    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;
    private Date deletedAt;

}
