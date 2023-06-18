package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.MerchantFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class ScannedFilesRequestDto {
    @Schema(description = "Must be five objects, the merchantId field can be omitted.")
    @NotNull
    private List<MerchantFileDto> files;

}
