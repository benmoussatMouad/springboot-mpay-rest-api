package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantStatus;
import com.springboot.mpaybackend.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Data
public class MerchantAccountTraceDto {
    private Long id;
    private String details;
    private String status;
    private String feedback;
    private Long merchantId;
    private String userUsername;
    private Long bankId;
    private Date createdAt;
}
