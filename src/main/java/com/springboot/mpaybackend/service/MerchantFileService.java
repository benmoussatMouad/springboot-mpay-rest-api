package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import com.springboot.mpaybackend.payload.MerchantFileResponseDto;

import java.io.IOException;
import java.util.List;

public interface MerchantFileService {

    MerchantFileResponseDto saveMerchantFile(MerchantFileDto file) ;

    MerchantFile getMerchantFileById(Long fileId);

    MerchantFileDto getMerchantFileInfo(Long id);

    List<MerchantFileResponseDto> getMerchantFilesByMerchantId(Long id);

    List<MerchantFileResponseDto> getNonRejectedMerchantFilesByMerchantId(Long id);

    void rejectFileById(Long fileId);
}
