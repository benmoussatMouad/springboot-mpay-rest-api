package com.springboot.mpaybackend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_trace_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "client_device_history_id")
    private DeviceHistory clientDeviceHistory;

    @ManyToOne
    @JoinColumn(name = "merchant_device_history_id")
    private DeviceHistory merchantDeviceHistory;


}
