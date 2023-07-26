package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.UserAgency;
import com.springboot.mpaybackend.entity.UserBank;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.payload.BankLightDto;
import com.springboot.mpaybackend.payload.BankPageDto;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.service.BankService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankServiceImpl implements BankService {

    private ModelMapper modelMapper;
    private BankRepository bankRepository;
    private WilayaRepository wilayaRepository;
    private UserBankRepository userBankRepository;
    private UserAgencyRepository userAgencyRepository;

    public BankServiceImpl(ModelMapper modelMapper, BankRepository bankRepository, WilayaRepository wilayaRepository, UserBankRepository userBankRepository, UserAgencyRepository userAgencyRepository) {
        this.modelMapper = modelMapper;
        this.bankRepository = bankRepository;
        this.wilayaRepository = wilayaRepository;
        this.userBankRepository = userBankRepository;
        this.userAgencyRepository = userAgencyRepository;
    }

    @Override
    public BankDto addBank(BankDto bankDto) {
        Bank bank = modelMapper.map( bankDto, Bank.class );

        bank.setBmtmCentralized( 0 );
        bank.setTotalLicence( 0 );
        bank.setTotalConsumedLicence( 0 );

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


        if(bankDto.getBankBin() != null) bank.setBankBin( bankDto.getBankBin() );
        if(bankDto.getBankCode() != null)  bank.setBankCode( bankDto.getBankCode() );
        if(bankDto.getAddress() != null) bank.setAddress( bankDto.getAddress() );
        if(bankDto.getAcronymName() != null) bank.setAcronymName( bankDto.getAcronymName() );
        if(bankDto.getWilayaId() != null) {

            Wilaya wilaya = wilayaRepository.findById( bankDto.getWilayaId() )
                    .orElseThrow(() -> new ResourceNotFoundException("Wilaya", "id", bankDto.getWilayaId()));
            bank.setWilaya( wilaya );
        }

        if(bankDto.getCommune() != null) bank.setCommune( bankDto.getCommune() );
        if(bankDto.getBmtmCentralized() != null) bank.setBmtmCentralized( bankDto.getBmtmCentralized() );
        if(bankDto.getFax() != null) bank.setFax( bankDto.getFax() );
        if(bankDto.getName() != null) bank.setName( bankDto.getName() );
        if(bankDto.getDeleteDate() != null) bank.setDeleteDate( bankDto.getDeleteDate() );

        Bank savedBank = bankRepository.save( bank );
        return modelMapper.map( savedBank, BankDto.class );
    }

    @Override
    public void deleteBank(java.lang.Long bankId) {
        Bank bank = bankRepository.findById( bankId )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "id", bankId ) );

        bankRepository.delete( bank );
    }

    @Override
    public List<BankLightDto> getBanksLightFormat() {
        List<Bank> banks = bankRepository.findAll();

        return banks.stream().map( (bank -> modelMapper.map( bank, BankLightDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public BankPageDto getBanks(int page, int size) {
        Page<Bank> banks = bankRepository.findAll( PageRequest.of( page, size ) );

        List<BankDto> bankDtoList = banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) ).toList();

        BankPageDto bankPage = new BankPageDto();

        bankPage.setCount( banks.getTotalElements() );
        bankPage.setBanks( bankDtoList );

        return bankPage;
    }

    @Override
    public List<BankDto> getBanksByNameContaining(String bankName) {
        List<Bank> banks = bankRepository.findByNameContaining(bankName);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByWilaya(Long wilayaId) {
        List<Bank> banks = bankRepository.findByWilayaId(wilayaId);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByCodeContaining(String bankCode) {
        List<Bank> banks = bankRepository.findByBankCodeContaining(bankCode);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByTotalLicenseLesserOrEqualThan(Integer maxLicense) {
        List<Bank> banks = bankRepository.findByTotalLicenceLessThanEqual(maxLicense);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByTotalLicenseGreaterOrEqualThan(Integer minLicense) {
        List<Bank> banks = bankRepository.findByTotalLicenceLessThanEqual(minLicense);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByAddressContaining(String address) {
        List<Bank> banks = bankRepository.findByAddressContaining(address);

        return banks.stream().map( (bank -> modelMapper.map( bank, BankDto.class )) )
                .collect( Collectors.toList() );
    }

    @Override
    public List<BankDto> getBanksByPhoneContaining(String phone) {
        List<Bank> banks = bankRepository.findByPhoneContaining(phone);

        return banks.stream().map((bank -> modelMapper.map(bank, BankDto.class)))
                .collect(Collectors.toList());
    }

    @Override
    public BankDto getBankForBankUser(String username) {
        UserBank userBank = userBankRepository.findByUsernameUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Bank user", "username", username));

        return modelMapper.map(userBank.getBank(), BankDto.class);
    }

    @Override
    public BankDto getBankForAgencyUser(String username) {
        UserAgency user = userAgencyRepository.findByUsernameUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Agency user", "username", username));

        return modelMapper.map(user.getAgency().getBank(), BankDto.class);
    }

}
