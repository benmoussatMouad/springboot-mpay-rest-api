package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlockRequestDto {
    @NotEmpty
    @NotNull
    private String cause;
    @NotEmpty
    @NotNull
    private String details;
}
