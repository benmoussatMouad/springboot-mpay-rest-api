package com.springboot.mpaybackend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tm_id")
    private Long id;

    private String terminalId;
    private String terminalLabel;
    private String terminalLine1Address;
    private String line2Address;
    private String line3Address;
    private String line4Address;
    private String line5Address;
    private String line6Address;
    private String line7Address;
    private String line8Address;
    private String terminalPhoneNumber;
    private String terminalFaxNumber;
    private String terminalMobileNumber;
    private String terminalMailAddress;
    private String trxDebit;
    private String trxRemb;
    private String trxSolde;
    private String trxAnnul;
    private String trxPAutor;
    private String trxPhone;
    private String trxWithdraw;
    private String trxCashAdvancing;
    private String trxBillPayment;
    private String startTime;
    private String endTime;
    private String updateCardTypeForTerminal;
    private String cardType;
    private String cardType1;
    private String cardType2;
    private String cardType3;
    private String cardType4;
    private String cardType5;
    private String cardType6;
    private String limit0;
    private String limit1;
    private String limit2;
    private String limit3;
    private String limit4;
    private String limit5;
    private String limit6;
    private String terminalType;
    private String enteteTm;
    private String debutInfoTm;
    private String finTm;
    private Integer flag;

    @OneToOne
    @JoinColumn(name = "bm_id")
    private Bm bm;
}
