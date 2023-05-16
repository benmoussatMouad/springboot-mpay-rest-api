package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Agency;
import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.AgencyDto;
import com.springboot.mpaybackend.payload.AgencyLightDto;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.WilayaRepository;
import com.springboot.mpaybackend.service.AgencyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgencyServiceImpl implements AgencyService {

    AgencyRepository agencyRepository;
    BankRepository bankRepository;
    WilayaRepository wilayaRepository;
    ModelMapper modelMapper;

    public AgencyServiceImpl(AgencyRepository agencyRepository, BankRepository bankRepository, WilayaRepository wilayaRepository, ModelMapper modelMapper) {
        this.agencyRepository = agencyRepository;
        this.bankRepository = bankRepository;
        this.wilayaRepository = wilayaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AgencyDto addAgency(AgencyDto agencyDto) {
        Agency agency = modelMapper.map( agencyDto, Agency.class );
        Agency savedAgency = agencyRepository.save( agency );

        return modelMapper.map( savedAgency, AgencyDto.class );
    }

    @Override
    public AgencyDto getAgency(java.lang.Long agencyId) {
        Agency agency = agencyRepository.findById( agencyId )
                .orElseThrow(() -> new ResourceNotFoundException( "Agency", "id", agencyId));
        return modelMapper.map( agency, AgencyDto.class );
    }

    @Override
    public List<AgencyDto> getAgencies() {
        List<Agency> agencies = agencyRepository.findAll();

        return agencies.stream().map( (agency -> modelMapper.map( agency, AgencyDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public List<AgencyDto> getAgenciesByBank(Long bankId) {

        List<Agency> agencies = agencyRepository.findByBankId(bankId);

        return agencies.stream().map( (agency -> modelMapper.map( agency, AgencyDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public AgencyDto updateAgency(AgencyDto agencyDto, java.lang.Long agencyId) {
        Agency agency = agencyRepository.findById( agencyId )
                .orElseThrow( () -> new ResourceNotFoundException( "Agency", "id", agencyId ) );
        if(agencyDto.getBankId() != null) {

            Bank bank = bankRepository.findById( agencyDto.getBankId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "bank", "id", agencyDto.getBankId() ) );
            agency.setBank( bank );
        }

        if( agencyDto.getWilayaId() != null ) {
            Wilaya wilaya = wilayaRepository.findById( agencyDto.getWilayaId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "Wilaya", "id", agencyDto.getWilayaId() ) );

            agency.setWilaya( wilaya );
        }

        if (agencyDto.getAddress() != null) agency.setAddress( agencyDto.getAddress() );
        if (agencyDto.getAgencyCode() != null) agency.setAgencyCode( agencyDto.getAgencyCode() );
        if (agencyDto.getAgencyName() != null) agency.setAgencyName( agencyDto.getAgencyName() );
        if (agencyDto.getCommune() != null) agency.setCommune( agencyDto.getCommune() );
        if (agencyDto.getFax() != null) agency.setFax( agencyDto.getFax() );
        if (agencyDto.getDeletedDate() != null) agency.setDeletedDate( agencyDto.getDeletedDate() );
        if (agencyDto.getFlag() != null) agency.setFlag( agencyDto.getFlag() );
        if (agencyDto.getPhone() != null) agency.setPhone( agencyDto.getPhone() );

        Agency savedAgency = agencyRepository.save( agency );

        return modelMapper.map( savedAgency, AgencyDto.class );
    }

    @Override
    public void deleteAgency(java.lang.Long agencyId) {
        agencyRepository.deleteById( agencyId );
    }

    @Override
    public List<AgencyLightDto> getAgenciesLightFormat() {
        List<Agency> agencies = agencyRepository.findAll();

        return agencies.stream().map( (agency -> modelMapper.map( agency, AgencyLightDto.class )) )
                .collect( Collectors.toList() );
    }
}
