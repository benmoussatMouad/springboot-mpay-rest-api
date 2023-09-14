package com.springboot.mpaybackend.payload;

import java.util.List;

import lombok.Data;

@Data
public class ClientMerchantStatisticsDto {
    private double turnOver;
    private List<GraphCouplesAmount> graph;
}
