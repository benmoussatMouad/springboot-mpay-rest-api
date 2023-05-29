package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Bank;
import lombok.Data;

import java.util.Date;

@Data
public class AgencyResponseDto {

    private Long id;
    private String agencyName;
    private String agencyCode;
    private String address;
    private Long wilayaId;
    private String wilayaName;

    private String commune;
    private String phone;
    private String fax;
    private Long bankId;
    private String bankName;
    private String bankAcronymName;

    private Integer flag;
    private Date deletedDate;
}
