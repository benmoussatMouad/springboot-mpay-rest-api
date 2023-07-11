package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.BmFileCheckDto;

public interface BmService {
    BmFileCheckDto verifyFileContent(String content);
}
