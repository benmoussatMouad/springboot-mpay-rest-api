package com.springboot.mpaybackend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankLightDto {

    public Long id;
    public String name;
    public String acronymName;
}
