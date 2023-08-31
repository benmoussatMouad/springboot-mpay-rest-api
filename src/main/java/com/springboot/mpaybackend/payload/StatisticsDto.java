package com.springboot.mpaybackend.payload;

import java.util.List;

import lombok.Data;

@Data
public class StatisticsDto {
    private Long totalTransactions;
    private double turnOver;
    private Long newMerchants;
    private Long activeMerchants;
    private Long failedTransactions;
    private Long succesfullTransactions;
    private Long canceledTransactions;
    private Long refundedTransactions;
    private Long nonActiveMerchants;
    private List<GraphCouples> graph;
}
