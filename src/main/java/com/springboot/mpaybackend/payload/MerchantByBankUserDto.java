package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class MerchantByBankUserDto {

    private String username;
    private String password;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String address;
    @NotEmpty
    private Long wilayaId;
    @NotEmpty
    private String commune;
    @NotEmpty
    private String postalCode;
    @NotEmpty
    private String phone;
    private String identityCardNumber;
    private String fiscalNumber;
    private String registreCommerceNumber;
    private String articleImpotsNumber;

    private String rib;

    private List<MerchantFileDto> documents;

}
