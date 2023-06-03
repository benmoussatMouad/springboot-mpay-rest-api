package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Bank;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantAccount;
import com.springboot.mpaybackend.entity.MerchantStatusTrace;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.MerchantAccountTraceDto;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.repository.MerchantAccountRepository;
import com.springboot.mpaybackend.repository.MerchantRepository;
import com.springboot.mpaybackend.repository.MerchantStatusTraceRepository;
import com.springboot.mpaybackend.service.MerchantAccountService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.springboot.mpaybackend.utils.AppConstants.SIZE_OF_BANK_CODE;

@Service
public class MerchantAccountServiceImpl implements MerchantAccountService {

    BankRepository bankRepository;
    MerchantAccountRepository merchantAccountRepository;
    MerchantRepository merchantRepository;
    MerchantStatusTraceRepository merchantStatusTraceRepository;
    ModelMapper modelMapper;


    public MerchantAccountServiceImpl(BankRepository bankRepository, MerchantAccountRepository merchantAccountRepository, MerchantRepository merchantRepository, MerchantStatusTraceRepository merchantStatusTraceRepository, ModelMapper modelMapper) {
        this.bankRepository = bankRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.merchantRepository = merchantRepository;
        this.merchantStatusTraceRepository = merchantStatusTraceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createAccountForMerchantByBankCode(Long merchantId, String rib) {

        MerchantAccount account = new MerchantAccount();
        Merchant merchant = merchantRepository.findById( merchantId )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", merchantId ) );

        String bankCode = rib.substring( 0, SIZE_OF_BANK_CODE );

        Bank bank = bankRepository.findByBankCode( bankCode )
                .orElseThrow( () -> new ResourceNotFoundException( "Bank", "Bank Code", bankCode ) );

        account.setAccountNumber( rib );
        account.setMerchant( merchant );
        account.setBalance( 0 );
        account.setBank( bank );
        account.setStatus( merchant.getStatus() );

        //Creating the first trace when creating
        this.createTraceForAccount( account );

        merchantAccountRepository.save( account );
    }

    public void createTraceForAccount(MerchantAccount account) {
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setCreatedAt( new Date() );
        trace.setMerchant( account.getMerchant() );
        trace.setBank( account.getBank() );
        trace.setUser( account.getMerchant().getUsername() );
        trace.setStatus( account.getStatus() );

        merchantStatusTraceRepository.save( trace );
    }

    public List<MerchantAccountTraceDto> getAllMerchantStatusTraces(Long merchantId) {
        List<MerchantStatusTrace> traceList = merchantStatusTraceRepository.findAllByMerchantIdOrderByCreatedAt(merchantId);

        return traceList.stream().map( e -> modelMapper.map( e, MerchantAccountTraceDto.class ) ).collect( Collectors.toList() );
    }
}
