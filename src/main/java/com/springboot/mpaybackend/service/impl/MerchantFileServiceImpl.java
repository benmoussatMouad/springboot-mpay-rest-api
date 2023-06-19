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

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public MerchantFileResponseDto saveMerchantFile(MerchantFileDto dto) {

        List<MerchantFile> files = merchantFileRepository.findByMerchantId( dto.getMerchantId() );

        MerchantFile merchantFile;

        if( files.stream().map( MerchantFile::getPiece ).toList().contains( FileType.valueOf( dto.getPiece() ) ) ) {
            merchantFile = files.stream().filter( f -> f.getPiece().equals( FileType.valueOf( dto.getPiece() ) ) ).findFirst().orElse( null );

        } else {
            merchantFile = new MerchantFile();
        }

        Merchant merchant = merchantRepository.findByIdAndDeletedFalse( dto.getMerchantId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "id", dto.getMerchantId() ) );

        merchantFile.setMerchant( merchant );
        merchantFile.setContent( Base64.getDecoder().decode(dto.getContent()) );
        merchantFile.setName( dto.getName() );

        merchantFileRepository.save( merchantFile );

        return modelMapper.map( merchantFile, MerchantFileResponseDto.class );
    }

    @Override
    public MerchantFile getMerchantFileById(Long fileId) {
        return merchantFileRepository.findById( fileId )
                .orElseThrow( () -> new ResourceNotFoundException( "Merchant File", "id", fileId ) );
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

    @Override
    public List<MerchantFileResponseDto> getMerchantFilesByMerchantId(Long id) {
        if( !merchantRepository.existsByIdAndDeletedFalse( id ) ) {
            throw new ResourceNotFoundException( "Merchant", "id", id );
        }

        List<MerchantFile> merchantFiles = merchantFileRepository.findByMerchantId( id );

        return getMerchantFileResponseDtos( merchantFiles );
    }

    @Override
    public List<MerchantFileResponseDto> getNonRejectedMerchantFilesByMerchantId(Long id) {
        if( !merchantRepository.existsByIdAndDeletedFalse( id ) ) {
            throw new ResourceNotFoundException( "Merchant", "id", id );
        }

        List<MerchantFile> merchantFiles = merchantFileRepository.findByMerchantIdAndRejectedFalse( id );

        return getMerchantFileResponseDtos( merchantFiles );
    }

    @Override
    public void rejectFileById(Long fileId) {
        MerchantFile file = merchantFileRepository.findById( fileId )
                .orElseThrow( () -> new ResourceNotFoundException( "File", "id", fileId ) );

        file.setRejected( true );

        merchantFileRepository.save( file );
    }

    private List<MerchantFileResponseDto> getMerchantFileResponseDtos(List<MerchantFile> merchantFiles) {
        return merchantFiles.stream().map( e -> {
            MerchantFileResponseDto dto = new MerchantFileResponseDto();
            dto.setMerchantId( e.getMerchant().getId() );
            dto.setName( e.getName() );
            dto.setId( e.getId() );
            dto.setPiece( e.getPiece().toString() );
            dto.setContent( Base64.getEncoder().encodeToString( e.getContent() ) );
            dto.setRejected( e.isRejected() );
            return dto;
        } ).collect( Collectors.toList());
    }
}
