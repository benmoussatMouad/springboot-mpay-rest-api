package com.springboot.mpaybackend.payload;

import com.springboot.mpaybackend.entity.FileType;
import com.springboot.mpaybackend.utils.EnumValidator;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MerchantFileDto {
    private String content;
    private Long merchantId;
    private String name;
    @EnumValidator( enumClass = FileType.class)
    private String piece;
}
