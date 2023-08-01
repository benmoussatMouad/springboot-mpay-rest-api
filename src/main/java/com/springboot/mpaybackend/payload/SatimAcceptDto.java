package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SatimAcceptDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String terminalId;
}
