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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(columnDefinition = "boolean default false")
    private boolean enabled;

    private String code;

    @ManyToOne
    @JoinColumn(name = "app_version_id")
    private AppVersion appVersion;

    private Integer employe;

    private String phone;
    private Integer suffersAttempts;
    private boolean firstConnexion;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private Date deletedDate;
    private Integer flag;
    private Integer connexion;
    private Date userStatusChangedOn;
    private Date maxAttemptDateReached;
    private Integer codeRequestAttempt;
    private String userStatus;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;
}
