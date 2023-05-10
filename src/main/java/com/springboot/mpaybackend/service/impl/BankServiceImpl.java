package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.WilayaRepository;
import com.springboot.mpaybackend.service.BankService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankServiceImpl implements BankService {

    private ModelMapper modelMapper;
    private BankRepository bankRepository;
    private WilayaRepository wilayaRepository;

    public BankServiceImpl(ModelMapper modelMapper, BankRepository bankRepository, WilayaRepository wilayaRepository) {
        this.modelMapper = modelMapper;
        this.bankRepository = bankRepository;
        this.wilayaRepository = wilayaRepository;
    }

    @Override
    public BankDto addBank(BankDto bankDto) {
        Bank bank = modelMapper.map( bankDto, Bank.class );
        Bank savedBank = bankRepository.save( bank );
        return modelMapper.map( savedBank, BankDto.class );
    }

    @Override
    public BankDto getBank(Long bankId) {

        Bank bank = bankRepository.findById( bankId )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", bankId ) );

        return modelMapper.map( bank, BankDto.class );
    }

    @Override
    public List<BankDto> getBanks() {

        List<Bank> banks = bankRepository.findAll();
        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList());
    }

    @Override
    public BankDto updateBank(BankDto bankDto, java.lang.Long bankId) {
        Bank bank = bankRepository.findById( bankId )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", bankId ) );
        Wilaya wilaya = wilayaRepository.findById( bankDto.getWilayaId() )
                .orElseThrow(() -> new ResourceNotFoundException("Wilaya", "id", bankDto.getWilayaId()));

        bank.setBankBin( bankDto.getBankBin() );
        bank.setBankCode( bankDto.getBankCode() );
        bank.setAddress( bankDto.getAddress() );
        bank.setAcronymName( bank.getAcronymName() );
        bank.setWilaya( wilaya );
        bank.setCommune( bankDto.getCommune() );
        bank.setBmtmCentralized( bankDto.getBmtmCentralized() );
        bank.setFax( bankDto.getFax() );
        bank.setName( bankDto.getName() );
        bank.setDeleteDate( bankDto.getDeleteDate() );

        Bank savedBank = bankRepository.save( bank );
        return modelMapper.map( savedBank, BankDto.class );
    }

    @Override
    public void deleteBank(java.lang.Long bankId) {
        Bank bank = bankRepository.findById( bankId )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", bankId ) );

        bankRepository.delete( bank );
    }
}
