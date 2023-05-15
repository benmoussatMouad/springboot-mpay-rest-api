package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
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
    private String firstName;
    @NotEmpty(message = "Name should not be null or empty")
    private String lastName;
    @NotEmpty(message = "Phone should not be null or empty")
    private String phone;

    @NotEmpty(message = "username should not be null or empty")
    private String username;

    @NotEmpty(message = "Name should not be null or empty")
    @Size(min = 8, message = "Size should be 8 characters or more")
    private String password;
    @NotEmpty(message = "WilayaId should not be null or empty")
    private Long wilayaId;

    @NotEmpty(message = "Address should not be null or empty")
    private String address;
    @NotEmpty(message = "Commune should not be null or empty")
    private String commune;
}
