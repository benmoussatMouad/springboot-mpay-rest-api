package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.ClientMerchantStatisticsDto;
import com.springboot.mpaybackend.payload.StatisticsDto;

public interface StatisticsService {

    StatisticsDto getAllStats();

    StatisticsDto getStatsByBank(String username);

    ClientMerchantStatisticsDto getStatsForMerchantsAndClient(String username);
}