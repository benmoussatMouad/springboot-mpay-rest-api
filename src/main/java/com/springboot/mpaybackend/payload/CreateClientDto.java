package com.springboot.mpaybackend.payload;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
public class CreateClientDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String commune;
    private String postalCode;
    @NotEmpty
    private String phone;
    private Long wilayaId;
}