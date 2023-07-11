package com.springboot.mpaybackend.payload;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BmTmFileDto {
    @NotEmpty
    @NotNull
    private String content;
}
