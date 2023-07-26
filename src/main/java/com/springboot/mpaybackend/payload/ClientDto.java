package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.Wilaya;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class ClientDto {

    private Long id;

    private String userUsername;

    private String firstName;
    private String lastName;
    private String address;

    private Long wilayaId;
    private String wilayaName;

    private String commune;
    private String postalCode;
    private String phone;
}
