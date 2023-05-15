package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserType;
import lombok.Data;

@Data
public class UserAdminDto {
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userType;
    private String username;
    private String password;
}
