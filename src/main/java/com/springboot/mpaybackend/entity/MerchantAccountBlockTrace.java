package com.springboot.mpaybackend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MerchantAccountBlockTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_account_block_trace_id")
    private Long id;

    private String cause;
    private String details;

    private boolean accountStatus;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private MerchantAccount account;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User createdBy;

    private Date createdOn;
}
