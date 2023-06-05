package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.MerchantStatus;
import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.utils.EnumValidator;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MerchantDto {

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
    private String numberCheckoutRequested;

    @EnumValidator(enumClass = MerchantStatus.class)
    private String status;

}
