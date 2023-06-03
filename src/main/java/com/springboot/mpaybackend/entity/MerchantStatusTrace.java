package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.build.HashCodeAndEqualsPlugin;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class MerchantStatusTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_status_trace_id")
    private Long id;

    private String details;

    @Enumerated(EnumType.STRING)
    private MerchantStatus status;

    private String feedback;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private Date createdAt;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;
}
