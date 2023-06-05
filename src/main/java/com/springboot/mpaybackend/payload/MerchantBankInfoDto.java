package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class MerchantBankInfoDto {
    private String rc;
    private String nif;
    private String rib;
    private String cni;
    private String ai;
    private Integer nbCheckout;
}
