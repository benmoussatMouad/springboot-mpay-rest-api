package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.AgencyDto;

import java.util.List;

public interface AgencyService {

    AgencyDto addAgency(AgencyDto agencyDto);

    AgencyDto getAgency(java.lang.Long agencyId);

    List<AgencyDto> getAgencies();

    List<AgencyDto> getAgenciesByBank(Long bankId);

    AgencyDto updateAgency(AgencyDto agencyDto, java.lang.Long agencyId);

    void deleteAgency(java.lang.Long agencyId);
}
