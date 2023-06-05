package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.Bm;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.Wilaya;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class MerchantResponseDto {
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String address;

    private Long wilayaId;
    private String wilayaName;
    private String commune;
    private String postalCode;
    private String phone;
    private Long bmId;
    private String identityCardNumber;
    private String fiscalNumber;
    private String registreCommerceNumber;
    private String articleImpotsNumber;
    private String numberCheckoutRequested;
    private String rib;
    private String terminalId;
    // private String licenseId;
    private String status;
    private boolean accountStatus;
    private boolean enabled;
}
