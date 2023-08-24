package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.TransactionStatus;
import lombok.Data;

@Data
public class TransactionDto {
    private Long id;
    private String status;
    private double amount;
    private double amountRefund;
    private Long clientId;
    private Long merchantId;
    private String merchantWilayaName;
    private Long merchantWilayaId;
    private String merchantPhone;
    private String merchantCommune;
    private String orderId;

}
