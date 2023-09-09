package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Client;
import com.springboot.mpaybackend.entity.TransactionStatus;
import lombok.Data;

@Data
public class TransactionDto {
    private Long id;
    private String status;
    private Double amount;
    private Double amountRefund;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String type;
    private String transactionDate;
    private String merchantFirstName;
    private String merchantLastName;
    private String pan;
    private Long merchantId;
    private String merchantWilayaName;
    private Long merchantWilayaId;
    private String merchantPhone;
    private String merchantCommune;
    private String orderId;

}
