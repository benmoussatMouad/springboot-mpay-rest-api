package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.List;

@Data
public class TransactionPage {
    private Long count;
    List<TransactionDto> page;
}
