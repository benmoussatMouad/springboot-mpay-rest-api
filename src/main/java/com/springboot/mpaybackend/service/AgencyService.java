package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.AgencyDto;
import com.springboot.mpaybackend.payload.AgencyLightDto;
import com.springboot.mpaybackend.payload.AgencyResponseDto;

import java.util.List;

public interface AgencyService {

    AgencyDto addAgency(AgencyDto agencyDto);

    AgencyDto getAgency(java.lang.Long agencyId);

    List<AgencyResponseDto> getAgencies();

    List<AgencyResponseDto> getAgenciesByWilaya(Long wilayaId);

    List<AgencyResponseDto> getAgenciesByBank(Long bankId);

    AgencyDto updateAgency(AgencyDto agencyDto, java.lang.Long agencyId);

    void deleteAgency(java.lang.Long agencyId);

    List<AgencyLightDto> getAgenciesLightFormat();

    List<AgencyResponseDto> getAgenciesByNameContaining(String agencyName);

    List<AgencyResponseDto> getAgenciesByCodeContaining(String agencyCode);

    List<AgencyResponseDto> getAgenciesByPhoneContaining(String phone);
}
