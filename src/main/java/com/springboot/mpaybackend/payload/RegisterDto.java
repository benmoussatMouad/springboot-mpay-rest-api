package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "Name should not be null or empty")
    @NotNull
    private String firstName;
    @NotEmpty(message = "Name should not be null or empty")
    @NotNull
    private String lastName;
    @NotEmpty(message = "Phone should not be null or empty")
    @NotNull
    private String phone;

    @NotEmpty(message = "username should not be null or empty")
    @NotNull
    private String username;

    @NotEmpty(message = "Name should not be null or empty")
    @Size(min = 8, message = "Size should be 8 characters or more")
    @NotNull
    private String password;

    @NotEmpty(message = "WilayaId should not be null or empty")
    @NotNull
    private Long wilayaId;

    @NotEmpty(message = "Address should not be null or empty")
    @NotNull
    private String address;

    @NotEmpty(message = "Commune should not be null or empty")
    @NotNull
    private String commune;
}
