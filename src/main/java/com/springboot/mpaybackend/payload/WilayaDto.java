package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class WilayaDto {

    private Long id;
    @NotEmpty
    private Integer number;
    @NotEmpty
    private String name;

}
