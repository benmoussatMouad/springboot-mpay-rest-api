package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.MerchantFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ScannedFilesRequestDto {
    @Schema(name = "Scanned merchant documents", description = "Must be five objects, the merchantId field can be omitted.")
    private List<MerchantFileDto> files;
}
