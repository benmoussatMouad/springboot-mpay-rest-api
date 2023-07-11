package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.TmFileCheckDto;

public interface TmService {
    TmFileCheckDto verifyFileContent(String content);
}
