package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.List;

@Data
public class UserBankPageDto {

    private Long count;
    private List<UserBankDto> userPage;
}
