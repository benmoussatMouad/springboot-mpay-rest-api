package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserBankDto {

    private Long id;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private String userType;
    private String username;
    private String password;
    private Long bankId;
}
