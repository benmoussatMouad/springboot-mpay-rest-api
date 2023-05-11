package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.payload.WilayaDto;
import com.springboot.mpaybackend.repository.WilayaRepository;
import com.springboot.mpaybackend.service.WilayaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WilayaServiceImpl implements WilayaService {

    WilayaRepository wilayaRepository;
    ModelMapper modelMapper;

    public WilayaServiceImpl(WilayaRepository wilayaRepository, ModelMapper modelMapper) {
        this.wilayaRepository = wilayaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WilayaDto addWilaya(WilayaDto dto) {
        Wilaya wilaya = modelMapper.map( dto, Wilaya.class );

        Wilaya savedWilaya = wilayaRepository.save( wilaya );

        return modelMapper.map( savedWilaya, WilayaDto.class );
    }

    @Override
    public WilayaDto getWilaya(Long wilayaId) {
        Wilaya wilaya = wilayaRepository.findById( wilayaId )
                .orElseThrow( () -> new ResourceNotFoundException( "Wilaya", "id", wilayaId ) );

        return modelMapper.map( wilaya, WilayaDto.class );
    }

    @Override
    public List<WilayaDto> getAllWilayas() {
        List<Wilaya> wilayas = wilayaRepository.findAll();

        return wilayas.stream().map( (wilaya -> modelMapper.map( wilaya, WilayaDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public WilayaDto updateWilaya(WilayaDto dto, Long wilayaId) {
        Wilaya wilaya = wilayaRepository.findById( wilayaId )
                .orElseThrow(() -> new ResourceNotFoundException("Wilaya", "id", wilayaId));
        wilaya.setName( dto.getName() );
        wilaya.setNumber( dto.getNumber() );
        Wilaya updatedWilaya = wilayaRepository.save( wilaya );

        return modelMapper.map(updatedWilaya, WilayaDto.class);
    }

    @Override
    public void deleteWilaya(Long wilayaId) {
        Wilaya wilaya = wilayaRepository.findById( wilayaId )
                .orElseThrow(() -> new ResourceNotFoundException("Wilaya", "id", wilayaId));

        wilayaRepository.delete( wilaya );
    }
}
