package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.FileType;
import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import com.springboot.mpaybackend.payload.MerchantFileResponseDto;
import com.springboot.mpaybackend.repository.MerchantFileRepository;
import com.springboot.mpaybackend.repository.MerchantRepository;
import com.springboot.mpaybackend.service.MerchantFileService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class MerchantFileServiceImpl implements MerchantFileService {

    ModelMapper modelMapper;
    MerchantFileRepository merchantFileRepository;
    MerchantRepository merchantRepository;

    public MerchantFileServiceImpl(ModelMapper modelMapper, MerchantFileRepository merchantFileRepository, MerchantRepository merchantRepository) {
        this.modelMapper = modelMapper;
        this.merchantFileRepository = merchantFileRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    public MerchantFileResponseDto saveMerchantFile(MerchantFileDto dto) throws IOException {
        MerchantFile merchantFile = new MerchantFile();
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( dto.getMerchantId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", dto.getMerchantId() ) );
        merchantFile.setMerchant( merchant );
        merchantFile.setContent( Base64.getDecoder().decode(dto.getContent()) );
        merchantFile.setPiece( FileType.valueOf( dto.getPiece() ) );
        merchantFile.setName( dto.getName() );

        merchantFileRepository.save( merchantFile );

        return modelMapper.map( merchantFile, MerchantFileResponseDto.class );
    }

    @Override
    public MerchantFile getMerchantFileById(Long fileId) {
        MerchantFile merchantFile = merchantFileRepository.findById( fileId )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant File", "id", fileId ) );
        return merchantFile;
    }

    @Override
    public MerchantFileDto getMerchantFileInfo(Long id) {
        MerchantFile file = merchantFileRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant File", "id", id ) );

        MerchantFileDto dto = new MerchantFileDto();
        dto.setMerchantId( file.getMerchant().getId() );
        dto.setName( file.getName() );
        dto.setContent( Base64.getEncoder().encodeToString( file.getContent() ) );
        dto.setPiece( file.getPiece().toString() );

        return dto;
    }
}
