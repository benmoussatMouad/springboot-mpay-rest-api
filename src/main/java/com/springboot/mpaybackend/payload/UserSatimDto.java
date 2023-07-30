package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserSatimDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String username;
    private String password;
    private Date createdAt;
    private String createdByUsername;
    private Date updatedAt;
    private String updatedByUsername;
}
