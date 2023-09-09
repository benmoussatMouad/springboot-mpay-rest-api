package com.springboot.mpaybackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.mpaybackend.payload.StatisticsDto;
import com.springboot.mpaybackend.service.StatisticsService;

@RestController
@RequestMapping("api/v1/statistics")
public class StatisticsController {

    StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN', 'SATIM', 'CLIENT', 'MERCHANT')")
    public ResponseEntity<StatisticsDto> getStats(Authentication authentication) {

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || authentication.getAuthorities().contains(new SimpleGrantedAuthority("SATIM"))) {
            return ResponseEntity.ok(statisticsService.getAllStats());
        } else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT")) || 
        authentication.getAuthorities().contains(new SimpleGrantedAuthority("MERCHANT"))) {
            return ResponseEntity.ok(statisticsService.getStatsForMerchantsAndClient(authentication.getName()));
        } 
        else {
            return ResponseEntity.ok(statisticsService.getStatsByBank(authentication.getName()));
        }

    }

}
