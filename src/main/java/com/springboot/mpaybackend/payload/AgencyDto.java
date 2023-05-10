package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.Date;

@Data
public class AgencyDto {

    private Long agencyId;
    private String agencyName;
    private String agencyCode;
    private String address;
    private Long wilayaId;

    private String commune;
    private String phone;
    private String fax;
    private Long bankId;

    private Integer flag;
    private Date deletedDate;
}
