package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.User;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Date;

@Data
public class UserAgencyDto {

    private Long id;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private String userType;
    private String username;
    private String password;
    private Long agencyId;

    // TODO: add UserAgency special admin response DTO

}
