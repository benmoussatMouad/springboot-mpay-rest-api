package com.springboot.mpaybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bm_id")
    private Long id;

    private String contractNumberMerchantBM;
    private String merchantSocialReason;
    private String merchantName;
    private String tradeCategory;
    private String merchantRib;
    private String agencyCode;
    private String webSiteAdress;
    private String documentTypeForBank;
    private String merchantNif;
    private String principleContactTitle;
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
    private String exercisingYearNumber;
    private String merchantSecondNameContact;
    private String merchantPhoneNumber;
    private String faxNumber;
    private String merchantMobilePhoneNumber;
    private String merchantMail;
    private String merchantAgencyLabel;
    private String merchantTransactionThreshold;
    private String enteteBm;
    @Column(columnDefinition = "text")
    private String debutInfoBm;
    private String finBm;
    private Integer automatic;


    @Column(columnDefinition = "boolean default false")
    private Boolean deleted;

    private Date CreatedOn;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;


}
