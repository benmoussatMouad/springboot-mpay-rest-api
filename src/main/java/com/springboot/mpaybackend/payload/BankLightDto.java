package com.springboot.mpaybackend.payload;

import lombok.Data;

@Data
public class BankLightDto {

    public Long id;
    public String name;
    public String acronymName;
}
