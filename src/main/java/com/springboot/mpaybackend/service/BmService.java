package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.BmFileCheckDto;
import com.springboot.mpaybackend.payload.BmTmFileDto;

public interface BmService {
    BmFileCheckDto verifyFileContent(String content);

    BmTmFileDto findByMerchantId(Long id);
}
