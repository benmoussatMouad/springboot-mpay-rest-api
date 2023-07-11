package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class BmDto {
    private String fileName;
    private String acquiringBankCode;
    private Long recordNumber;
    private String merchantUpdateCode;
    private String merchantContractNumber;
    private String requiredDocumentTypeForBank;
    private String merchantTaxId;
    private String merchantSocialReason;
    private String merchantName;
    private String exercisingYearNumber;
    private String principleContactName;
    private String contactType;
    private String principleContactTitle;
    private String secondContactName;
    private String rcNumber;
    private String line1TradeAddress;
    private String line2TradeAddress;
    private String line3Address;
    private String line4Address;
    private String line5Address;
    private String line6Address;
    private String line7Address;
    private String line8Address;
    private String tradeLabel;
    private String merchantSecondNameContact;
    private String merchantPhoneNumber;
    private String faxNumber;
    private String merchantMobilePhoneNumber;
    private String merchantMail;
    private String merchantAgencyLabel;
    private String bankAccountNumber;
    private String bankAgencyCode;
    private String bankAgencyLabel;
    private String websiteAddress;
    private String specialCharacterHandling;
    private String bankCardThreshold;
    private String recordEndCharacter;
}
