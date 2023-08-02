package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.BmFileCheckDto;
import com.springboot.mpaybackend.payload.BmTmFileDto;
import com.springboot.mpaybackend.payload.TmFileCheckDto;
import com.springboot.mpaybackend.service.BmService;
import com.springboot.mpaybackend.service.TmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BmTmController {

    private BmService bmService;
    private TmService tmService;

    public BmTmController(BmService bmService, TmService tmService) {
        this.bmService = bmService;
        this.tmService = tmService;
    }

    @PostMapping("bm/check")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN')")
    public ResponseEntity<BmFileCheckDto> verifyBmFile(@RequestBody BmTmFileDto dto) {

        BmFileCheckDto response = bmService.verifyFileContent(dto.getContent());

        return ResponseEntity.ok(response);
    }

    @PostMapping("tm/check")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN')")
    public ResponseEntity<TmFileCheckDto> verifyTmFile(@RequestBody BmTmFileDto dto) {
        TmFileCheckDto response = tmService.verifyFileContent(dto.getContent());

        return ResponseEntity.ok(response);
    }

    @GetMapping("bm/merchant/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN', 'SATIM')")
    public ResponseEntity<BmTmFileDto> getBmFileByMerchantId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bmService.findByMerchantId(id));
    }
    @GetMapping("tm/merchant/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN', 'SATIM')")
    public ResponseEntity<BmTmFileDto> getTmFileByMerchantId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tmService.findByMerchantId(id));
    }
}
