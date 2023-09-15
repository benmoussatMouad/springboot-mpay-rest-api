package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.ClientMerchantStatisticsDto;
import com.springboot.mpaybackend.payload.GraphCouples;
import com.springboot.mpaybackend.payload.GraphCouplesAmount;
import com.springboot.mpaybackend.repository.*;
import org.springframework.stereotype.Service;

import com.springboot.mpaybackend.payload.StatisticsDto;
import com.springboot.mpaybackend.service.StatisticsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private TransactionRepository transactionRepository;
    private MerchantRepository merchantRepository;
    private UserRepository userRepository;
    private UserBankRepository userBankRepository;
    private UserAgencyRepository userAgencyRepository;
    private ClientRepository clientRepository;

    public StatisticsServiceImpl(TransactionRepository transactionRepository, MerchantRepository merchantRepository, UserRepository userRepository, UserBankRepository userBankRepository, UserAgencyRepository userAgencyRepository, ClientRepository clientRepository) {
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
        this.userBankRepository = userBankRepository;
        this.userAgencyRepository = userAgencyRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public StatisticsDto getAllStats() {
        StatisticsDto dto = new StatisticsDto();

        dto.setTotalTransactions(transactionRepository.countByDeletedFalse());
        dto.setFailedTransactions(transactionRepository.countByStatusAndDeletedFalse(TransactionStatus.FAILED));
        dto.setCanceledTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalse(TransactionStatus.CONFIRMED, TransactionType.CANCELLATION));

        Long newMerchantsNumber = merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.FILLED_INFO) + merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.NON_VERIFIED) + merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.IN_PROGRESS);
        dto.setNewMerchants(newMerchantsNumber);

        dto.setActiveMerchants(merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.VERIFIED));

        dto.setRefundedTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalse(TransactionStatus.CONFIRMED, TransactionType.REFUND));

        dto.setSuccesfullTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalse(TransactionStatus.CONFIRMED, TransactionType.PAYMENT));

        // Get last year
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        Date prevYearTime = prevYear.getTime();
        dto.setTurnOver(transactionRepository.calculateYearlyTurnOver(prevYearTime));
        dto.setNonActiveMerchants( newMerchantsNumber + merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.SATIM_ACCEPTED) +
                merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.ACCEPTED) +
                        merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.REVIEW) +
                        merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.SATIM_REJECTED)+
                        merchantRepository.countByStatusAndDeletedFalse(MerchantStatus.REJECTED));

        List<GraphCouples> graph = new ArrayList<>();
        // Looping through all the months since last year
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            Date todayDate = today.getTime();

            // Creating month range
            Calendar beginRanegCal = (Calendar) today.clone();
            beginRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1, 0, 0, 0);
            Date beginRange = beginRanegCal.getTime();

            Calendar endRanegCal = (Calendar) today.clone();
            endRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 31, 23, 59, 59);
            Date endRange = endRanegCal.getTime();

            System.out.println(beginRange);
            System.out.println(endRange);

            graph.add(new GraphCouples(todayDate, transactionRepository.countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfter(TransactionStatus.CONFIRMED, TransactionType.PAYMENT, endRange, beginRange)));
            today.add(Calendar.MONTH, -1);
        }

        dto.setGraph(graph);


        return dto;
    }

    @Override
    public StatisticsDto getStatsByBank(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));
        Bank bank = null;
        if (user.getUserType().equals(UserType.BANK_USER) || user.getUserType().equals(UserType.BANK_ADMIN)) {
            UserBank userBank = userBankRepository.findByUsernameUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user bank", "username", username));
            bank = userBank.getBank();
        } else {
            UserAgency userAgency = userAgencyRepository.findByUsernameUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("user agency", "username", username));

            bank = userAgency.getAgency().getBank();
        }

        StatisticsDto dto = new StatisticsDto();

        dto.setTotalTransactions(transactionRepository.countByDeletedFalseForBank(bank));
        dto.setFailedTransactions(transactionRepository.countByStatusAndDeletedFalseForBank(TransactionStatus.FAILED, bank));
        dto.setCanceledTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalseForBank(TransactionStatus.CONFIRMED, TransactionType.CANCELLATION, bank));

        Long newMerchantsNumber = merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.FILLED_INFO, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.NON_VERIFIED, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.IN_PROGRESS, bank);
        dto.setNewMerchants(newMerchantsNumber);

        dto.setActiveMerchants(merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.VERIFIED, bank));

        dto.setRefundedTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalseForBank(TransactionStatus.CONFIRMED, TransactionType.REFUND, bank));

        dto.setSuccesfullTransactions(transactionRepository.countByStatusAndTypeAndDeletedFalseForBank(TransactionStatus.CONFIRMED, TransactionType.PAYMENT, bank));

        dto.setNonActiveMerchants(newMerchantsNumber +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.SATIM_ACCEPTED, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.ACCEPTED, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.REVIEW, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.SATIM_REJECTED, bank) +
                merchantRepository.countByStatusAndDeletedFalseAndBank(MerchantStatus.REJECTED, bank));


        // Get last year
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        Date prevYearTime = prevYear.getTime();
        dto.setTurnOver(transactionRepository.calculateYearlyTurnOverForBank(prevYearTime, bank));

        List<GraphCouples> graph = new ArrayList<>();
        // Looping through all the months since last year
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            Date todayDate = today.getTime();

            Calendar beginRanegCal = (Calendar) today.clone();
            beginRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1, 0, 0, 0);
            Date beginRange = beginRanegCal.getTime();
            
            // Creating month range
            Calendar endRanegCal = (Calendar) today.clone();
            endRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 31, 23, 59, 59);
            Date endRange = endRanegCal.getTime();

            System.out.println(beginRange);
            System.out.println(endRange);

            graph.add(new GraphCouples(todayDate, transactionRepository.countByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchantBank(TransactionStatus.CONFIRMED, TransactionType.PAYMENT, endRange, beginRange, bank)));
            today.add(Calendar.MONTH, -1);
        }

        dto.setGraph(graph);


        return dto;
    }

    @Override
    public ClientMerchantStatisticsDto getStatsForMerchantsAndClient(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));

        Client client= null;
        Merchant merchant = null;
        ClientMerchantStatisticsDto dto = new ClientMerchantStatisticsDto(); 

        if(user.getUserType().equals(UserType.CLIENT)) {
            client = clientRepository.findByUserUsernameAndDeletedFalse(username)
            .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));
            Calendar prevYear = Calendar.getInstance();
            prevYear.add(Calendar.DATE, -7);
            Date lastWeek = prevYear.getTime();
            dto.setTurnOver(transactionRepository.calculateWeeklyTurnOverAndClient(lastWeek, username));

            List<GraphCouplesAmount> graph = new ArrayList<>();
            // Looping through all the days since last week
            Calendar today = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                Date todayDate = today.getTime();

                Calendar beginRanegCal = (Calendar) today.clone();
                beginRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                Date beginRange = beginRanegCal.getTime();
                
                // Creating month range
                Calendar endRanegCal = (Calendar) today.clone();
                endRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                Date endRange = endRanegCal.getTime();

                System.out.println(beginRange);
                System.out.println(endRange);

                graph.add(new GraphCouplesAmount(todayDate, 
                            transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndClient(TransactionStatus.CONFIRMED, TransactionType.PAYMENT, endRange, beginRange, client)
                             - transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndClient(TransactionStatus.CONFIRMED, TransactionType.REFUND, endRange, beginRange, client)
                             - transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndClient(TransactionStatus.CONFIRMED, TransactionType.CANCELLATION, endRange, beginRange, client)
                            ));
                today.add(Calendar.DATE, -1);
        }   

        dto.setGraph(graph);
        } else {
            merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse(username)
            .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));


            Calendar prevYear = Calendar.getInstance();
            prevYear.add(Calendar.DATE, -7);
            Date lastWeek = prevYear.getTime();
            dto.setTurnOver(transactionRepository.calculateWeeklyTurnOverAndMerchant(lastWeek, username));

            List<GraphCouplesAmount> graph = new ArrayList<>();
            // Looping through all the days since last week
            Calendar today = Calendar.getInstance();
            for (int i = 0; i < 7; i++) {
                Date todayDate = today.getTime();

                Calendar beginRanegCal = (Calendar) today.clone();
                beginRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                Date beginRange = beginRanegCal.getTime();
                
                // Creating month range
                Calendar endRanegCal = (Calendar) today.clone();
                endRanegCal.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                Date endRange = endRanegCal.getTime();

                System.out.println(beginRange);
                System.out.println(endRange);

                graph.add(new GraphCouplesAmount(todayDate, 
                transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchant(TransactionStatus.CONFIRMED, TransactionType.PAYMENT, endRange, beginRange, merchant)
                - transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchant(TransactionStatus.CONFIRMED, TransactionType.CANCELLATION, endRange, beginRange, merchant)
                - transactionRepository.sumAmountByStatusAndTypeAndDeletedFalseAndTransactionDateBeforeAndTransactionDateAfterAndMerchant(TransactionStatus.CONFIRMED, TransactionType.REFUND, endRange, beginRange, merchant)
                 
                ));
                today.add(Calendar.DATE, -1);
        }   

        dto.setGraph(graph);
        }


        
        return dto;
    }

    

}
