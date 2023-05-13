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
public class UserGie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGieId;
    private String firstName;
    private String lastName;
    private String phone;
    private String user_type;

    @OneToOne
    @JoinColumn(name = "username",referencedColumnName = "username")
    private User username;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    private Date createdAt;
    private Date updatedAt;
    private boolean deleted;
    private Date deletedAt;

}
