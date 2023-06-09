package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.MerchantFile;
import com.springboot.mpaybackend.payload.MerchantFileDto;
import com.springboot.mpaybackend.payload.MerchantFileResponseDto;
import com.springboot.mpaybackend.service.MerchantFileService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/file")
public class FileController {

    private MerchantFileService merchantFileService;

    public FileController(MerchantFileService merchantFileService) {
        this.merchantFileService = merchantFileService;
    }

    @PostMapping()
    public ResponseEntity<String> uploadFile(@RequestBody MerchantFileDto fileDto) {
        try {
            merchantFileService.saveMerchantFile( fileDto );

            return ResponseEntity.ok( "File uploaded successfully" );
        } catch (Exception e) {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( "Failed to read multipart file" );
        }
    }

    @GetMapping("download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // Retrieve the file from the database based on the fileId
            MerchantFile merchantFile = merchantFileService.getMerchantFileById( fileId );

            if( merchantFile == null ) {
                return ResponseEntity.notFound().build();
            }

            // Create a ByteArrayResource from the file content
            ByteArrayResource resource = new ByteArrayResource( merchantFile.getContent() );

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + merchantFile.getName() )
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .body( resource );
        } catch (Exception e) {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @GetMapping("info/{id}")
    public ResponseEntity<MerchantFileDto> getFile(@PathVariable Long id) {
        MerchantFileDto dto = merchantFileService.getMerchantFileInfo( id );

        return ResponseEntity.ok( dto );

    }

    @GetMapping("")
    public ResponseEntity<List<MerchantFileResponseDto>> getFilesBy(
            @RequestParam("merchant_id")
            @Parameter(required = true, description = "The merchant's ID by which to filter the files")
                Long merchantId
    ) {
        return ResponseEntity.ok( merchantFileService.getMerchantFilesByMerchantId( merchantId ) );
    }
}
