package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.Date;

@Data
public class BankDto {

    private Long id;
    private String name;
    private String acronymName;
    private String bankCode;
    private String address;
    private Long wilayaId;
    private String wilayaName;
    private String commune;
    private String phone;
    private String fax;
    private Integer totalLicence;
    private Integer totalConsumedLicence;
    private Date deleteDate;
    private Boolean bmtmCentralized;
    private Boolean validationCentralized;
    private String bankBin;
}
