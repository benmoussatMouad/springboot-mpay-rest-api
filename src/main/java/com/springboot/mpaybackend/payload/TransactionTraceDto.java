package com.springboot.mpaybackend.payload;
import java.util.Date;

import lombok.Data;

@Data
public class TransactionTraceDto {

    private Long id;
    private String status;
    private Date createdAt;
}
