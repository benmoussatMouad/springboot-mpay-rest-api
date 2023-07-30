package com.springboot.mpaybackend.payload;

import lombok.Data;

import java.util.List;

@Data
public class UserSatimPageDto {
    private Long count;
    private List<UserSatimDto> userPage;
}
