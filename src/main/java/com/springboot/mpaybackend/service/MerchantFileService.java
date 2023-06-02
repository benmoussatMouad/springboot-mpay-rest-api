package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import com.springboot.mpaybackend.payload.MerchantFileResponseDto;

import java.io.IOException;

public interface MerchantFileService {

    MerchantFileResponseDto saveMerchantFile(MerchantFileDto file) throws IOException;

    MerchantFile getMerchantFileById(Long fileId);

    MerchantFileDto getMerchantFileInfo(Long id);
}
