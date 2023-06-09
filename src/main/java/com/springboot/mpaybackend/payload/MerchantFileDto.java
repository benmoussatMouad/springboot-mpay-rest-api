package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.FileType;
import com.springboot.mpaybackend.utils.EnumValidator;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MerchantFileDto {
    @NotEmpty
    private String content;
    private Long merchantId;
    @NotEmpty
    private String name;
    @EnumValidator( enumClass = FileType.class)
    private String piece;
    private boolean rejected;
}
