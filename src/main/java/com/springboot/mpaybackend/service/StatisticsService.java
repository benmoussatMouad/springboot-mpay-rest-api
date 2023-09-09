package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.StatisticsDto;

public interface StatisticsService {

    StatisticsDto getAllStats();

    StatisticsDto getStatsByBank(String username);

    StatisticsDto getStatsForMerchantsAndClient(String username);
}