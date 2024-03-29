package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.FileType;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.MerchantAccountService;
import com.springboot.mpaybackend.service.MerchantFileService;
import com.springboot.mpaybackend.service.MerchantService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.springboot.mpaybackend.utils.AppConstants.MERCHANT_FILES_NUMBER;

@RestController
@RequestMapping("api/v1/merchant")
public class MerchantController {
    private static final Logger logger = LoggerFactory.getLogger( MerchantController.class );


    private MerchantService merchantService;
    private MerchantAccountService merchantAccountService;
    private MerchantFileService merchantFileService;

    public MerchantController(MerchantService merchantService, MerchantAccountService merchantAccountService, MerchantFileService merchantFileService) {
        this.merchantService = merchantService;
        this.merchantAccountService = merchantAccountService;
        this.merchantFileService = merchantFileService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MERCHANT', 'AGENCY_USER', 'AGENCY_ADMIN', 'BANK_USER', 'BANK_ADMIN', 'SATIM')")
    public ResponseEntity<MerchantResponseDto> createMerchant(@RequestBody MerchantDto dto, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
        || authentication.getAuthorities().contains(new SimpleGrantedAuthority("MERCHANT"))) {
            return ResponseEntity.ok(merchantService.addMerchant(dto, false, authentication.getName()));
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("BANK_USER"))
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("BANK_ADMIN"))
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("AGENCY_USER"))
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("AGENCY_ADMIN"))) {

            return ResponseEntity.ok(merchantService.addMerchant(dto, true, authentication.getName()));
        }
        return ResponseEntity.ok(merchantService.addMerchant(dto, false, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponseDto>> getUsersAgency() {

        return ResponseEntity.ok( merchantService.getAllMerchants() );
    }

    @GetMapping("trace/{merchantId}")
    public ResponseEntity<List<MerchantAccountTraceDto>> getMerchantTraces(@PathVariable("merchantId") Long id) {

        return ResponseEntity.ok( merchantAccountService.getAllMerchantStatusTraces( id ) );
    }


    @GetMapping("page")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'BANK_USER', 'BANK_ADMIN', 'AGENCY_USER', 'AGENCY_ADMIN','SATIM')")
    public ResponseEntity<MerchantPageDto> getMerchantsByPageByFilter(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name = "size")
            @Parameter(description = "The size of the page") Integer size,
            @RequestParam(name = "name", required = false)
            @Parameter(description = "Filter the results by name containing") String name,
            @RequestParam(name = "phone", required = false)
            @Parameter(description = "Filter the results by phone containing") String phone,
            @RequestParam(name = "status", required = false)
            @Parameter(description = "Filter the results by user type") String status,
            @RequestParam(name = "reg_commerce", required = false)
            @Parameter(description = "Filter the results by name containing") String regCommerce,
            @RequestParam(name = "nif", required = false)
            @Parameter(description = "Filter the results by name containing") String nif,
            @RequestParam(name = "id", required = false)
            @Parameter(description = "The id of merchant within the table") Long id,
            Authentication authentication
    ) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        try {
            if (authorities.contains(new SimpleGrantedAuthority("ADMIN")) || authorities.contains(new SimpleGrantedAuthority("SATIM"))) {
                return ResponseEntity.ok(merchantService.getAllMerchantsByFilter(page, size, id, name, name, phone, regCommerce, nif, status));
            } else { // Calling user is either a bank user or agency user, get only related merchants
                return ResponseEntity.ok(merchantService.getAllMerchantsByFilterForSpecificBank(page, size, id, name, name, phone, regCommerce, nif, status, authentication.getName()));

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @GetMapping("{key}")
    public ResponseEntity<MerchantResponseDto> getMerchantByKey(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok( merchantService.getMerchant( id ) );
            case "username":
                return ResponseEntity.ok( merchantService.getMerchantByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<MerchantResponseDto> updateMerchant(@RequestBody MerchantDto dto,
                                                              @PathVariable Long id) {

        return ResponseEntity.ok( merchantService.updateMerchant( dto, id ) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserAgency(@PathVariable("id") Long id) {
        merchantService.deleteMerchant( id );

        return ResponseEntity.ok( "Merchant deleted successfully" );
    }

    @PutMapping("{id}/fill")
    public ResponseEntity<MerchantResponseDto> fillMerchantInfo(@RequestBody MerchantBankInfoDto dto, @PathVariable Long id) {

        return ResponseEntity.ok( merchantService.fillInfo( dto, id ) );
    }

    @PutMapping("{id}/block")
    public ResponseEntity<String> blockMerchantAccount(@PathVariable Long id, BlockRequestDto dto, Authentication authentication) {
        merchantService.blockMerchantAccount( id, dto, authentication.getName() );

        return ResponseEntity.ok( "Merchant Account has been disabled" );

    }

    @PutMapping("{id}/unblock")
    public ResponseEntity<String> unBlockMerchantAccount(@PathVariable Long id, BlockRequestDto dto, Authentication authentication) {
        merchantService.unBlockMerchantAccount( id, dto, authentication.getName() );

        return ResponseEntity.ok( "Merchant Account has been enabled" );

    }

    @PutMapping("{id}/in-progress")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('BANK_ADMIN') OR hasAuthority('MERCHANT')")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<MerchantDto> putMerchantFiles(@PathVariable Long id, @RequestBody ScannedFilesRequestDto dto) {

        // Get existing files
        List<MerchantFileResponseDto> existingFiles = merchantFileService.getNonRejectedMerchantFilesByMerchantId( id );
        int existingFilesNumber = (existingFiles == null) ? 0 : existingFiles.size();

        // Check if size of docs is as needed
        if( dto.getFiles().size() != (MERCHANT_FILES_NUMBER - existingFilesNumber) ) {

            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "Number of documents must be " + (MERCHANT_FILES_NUMBER - existingFilesNumber) );
        }

        // Check if all files are being sent, first get existing uploaded files, and check what is needed
        Set<FileType> uniqueFileTypes = new HashSet<>();
        for (MerchantFileResponseDto file : existingFiles) {
            FileType type = FileType.valueOf( file.getPiece() );

            //adding the existing file types to the set
            uniqueFileTypes.add( type );
        }

        // Checking if the all files have been included and not duplicated
        for (MerchantFileDto file : dto.getFiles()) {
            FileType type = FileType.valueOf( file.getPiece() );

            if( uniqueFileTypes.contains( type ) && !file.isRejected() ) {
                throw new MPayAPIException( HttpStatus.BAD_REQUEST, "File : " + type + " already exists or is duplicated" );
            } else {
                uniqueFileTypes.add( type );
            }
        }

        for (MerchantFileDto file : dto.getFiles()) {
            file.setMerchantId( id );
            merchantFileService.saveMerchantFile( file );
        }

        // Setting merchant status to IN_PROGRESS
        return ResponseEntity.ok( merchantService.putInProgress( id ) );
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAuthority('BANK_USER') OR hasAuthority('BANK_ADMIN') OR hasAuthority('ADMIN')")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<MerchantDto> demandReviewForMerchantInfos(@PathVariable Long id, @RequestBody FilesReviewDemandDto dto) {

        // Get existing files
        List<MerchantFileResponseDto> existingFiles = merchantFileService.getNonRejectedMerchantFilesByMerchantId( id );
        int existingFilesNumber = (existingFiles == null) ? 0 : existingFiles.size();

        // Checking if files Ids correspond to the merchant
        List<Long> ids = existingFiles.stream().map( MerchantFileResponseDto::getId ).toList();
        if( !ids.containsAll( dto.getIdFilesToReview() ) ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "Files do not belong to the target merchant" );
        }

        // Put files to Rejected
        for (Long fileId :
                dto.getIdFilesToReview()) {
            merchantFileService.rejectFileById( fileId );
        }

        // change status and save trace with feedback
        return ResponseEntity.ok( merchantService.demandReviewFile( id, dto.getFeedback() ) );
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('BANK_USER') OR hasAuthority('BANK_ADMIN') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> rejectMerchantDemand(@PathVariable Long id) {
        return ResponseEntity.ok( merchantService.rejectMerchant( id ) );
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasAnyAuthority('BANK_USER', 'BANK_ADMIN', 'ADMIN')")
    public ResponseEntity<MerchantDto> validateMerchant(@PathVariable Long id, @RequestBody AcceptMerchantDemandDto dto, Authentication authentication) {
        return ResponseEntity.ok(merchantService.validateMerchant(id, dto, authentication.getName()));
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasAuthority('BANK_USER') OR hasAuthority('BANK_ADMIN') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> acceptMerchantDemand(@PathVariable Long id) {

        return ResponseEntity.ok(merchantService.acceptMerchantByBank(id));
    }

    @PutMapping("/{id}/review-satim")
    @PreAuthorize("hasAuthority('SATIM') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> setBackTo(@PathVariable Long id, @RequestBody @Valid SatimReviewDto dto, Authentication authentication) {

        return ResponseEntity.ok(merchantService.putToSatimReview(id, dto, authentication.getName()));
    }

    @PutMapping("/{id}/accepted-satim")
    @PreAuthorize("hasAuthority('SATIM') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> acceptBySatin(@PathVariable Long id, @RequestBody @Valid SatimAcceptDto dto, Authentication authentication) {

        return ResponseEntity.ok(merchantService.putToSatimAccepted(id, dto, authentication.getName()));
    }
    @PutMapping("/{id}/rejected-satim")
    @PreAuthorize("hasAuthority('SATIM') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> rejectBySatim(@PathVariable Long id, @RequestBody @Valid SatimAcceptDto dto, Authentication authentication) {

        return ResponseEntity.ok(merchantService.putToSatimrejected(id, dto, authentication.getName()));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasAnyAuthority('BANK_ADMIN', 'BANK_USER', 'AGENCY_USER', 'AGENCY_ADMIN') OR hasAuthority('ADMIN')")
    public ResponseEntity<MerchantDto> verifyMerchant(@PathVariable Long id, Authentication authentication) {

        return ResponseEntity.ok(merchantService.verifyMerchant(id, authentication.getName()));
    }


}

