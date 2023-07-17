package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AcceptMerchantDemandDto {
    @NotEmpty
    private String BmContent;
    @NotEmpty
    private String TmContent;
}
