package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.BankService;
import com.springboot.mpaybackend.service.MerchantAccountService;
import com.springboot.mpaybackend.service.MerchantFileService;
import com.springboot.mpaybackend.service.MerchantService;
import com.springboot.mpaybackend.utils.Base64Checker;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private BankService bankService;
    private MerchantService merchantService;
    private MerchantFileService merchantFileService;
    private ModelMapper modelMapper;
    private MerchantAccountService merchantAccountService;

    public BankController(BankService bankService, MerchantService merchantService, MerchantFileService merchantFileService, ModelMapper modelMapper, MerchantAccountService merchantAccountService) {
        this.bankService = bankService;
        this.merchantService = merchantService;
        this.merchantFileService = merchantFileService;
        this.modelMapper = modelMapper;
        this.merchantAccountService = merchantAccountService;
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BankDto> addBank(@RequestBody BankDto bankDto){
        BankDto savedBank = bankService.addBank(bankDto);
        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }

    @PostMapping("merchant")
    //@PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<String> addMerchantThroughBank(@RequestBody MerchantByBankUserDto dto) {

        try {
            // Check if files exists
            if( dto.getDocuments() == null ) {
                return ResponseEntity.badRequest().body( "Documents not sent" );
            }
            if( dto.getDocuments().size() != 5 ) {
                return ResponseEntity.badRequest().body( "Number of documents must be 5" );
            } else {
                for (MerchantFileDto document :
                        dto.getDocuments()) {
                    if( !Base64Checker.isBase64( document.getContent() ) ) {
                        return ResponseEntity.badRequest().body( "File string must be base64 encoded" );
                    }
                }
            }

            //Create Merchant
            MerchantResponseDto merchantResponseDto = merchantService.addMerchant( modelMapper.map( dto, MerchantDto.class ), true );

            //Save Merchant files
            for (MerchantFileDto document :
                    dto.getDocuments()) {

                document.setMerchantId( merchantResponseDto.getId() );
                merchantFileService.saveMerchantFile( document );
            }

            // Create Merchant account
            merchantAccountService.createAccountForMerchantByBankCode( merchantResponseDto.getId(), dto.getRib() );

            return ResponseEntity.ok( "Merchant saved" );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body( "Error saving file" );
        }
    }

    @GetMapping()
    public ResponseEntity<List<BankDto>> getBanks(
            @RequestParam(name = "name",required = false)
            @Parameter(description = "if this is not null, this will filter the result to bank having this string in their name", example = "Exterieur give you Banque Exterieur d'algerie")
                    String bankName,
            @RequestParam(name = "wilaya_id",required = false)
            @Parameter(description = "if this is not null, this will filter the result by wilaya id", example = "1")
                    Long wilayaId,
            @RequestParam(name = "bank_code",required = false)
            @Parameter(description = "if this is not null, this will filter the result to banks containing this code in their codename", example = "BE, returns BEA and others")
                    String bankCode,
            @RequestParam(name = "min_license",required = false)
            @Parameter(description = "if this is not null, this will filter the result to banks having a total license number greated or equal to this value", example = "4 -> might return banks with total license of 4,5,6,...")
                    Integer minLicense,
            @RequestParam(name = "max_license",required = false)
            @Parameter(description = "if this is not null, this will filter the result to banks having a total license number lesser or equal to this value",
            example = "4 -> might return banks with total license of 4,3,...")
                    Integer maxLicense,
            @RequestParam(name = "address",required = false)
            @Parameter(description = "if this is not null, this will filter the result by banks containing this in their address")
                    String address,
            @RequestParam(name = "phone",required = false)
            @Parameter(description = "if this is not null, this will filter the result by banks containing this in their phone number")
                    String phone,
            @RequestParam(name = "id",required = false)
            @Parameter(description = "if this is not null, this will filter ")
                    Long id
    ) {
        List<BankDto> finalDto = bankService.getBanks();

        if( id != null ) {
            BankDto bank = bankService.getBank( id );
            finalDto = finalDto.stream().distinct().filter( bank::equals ).collect( Collectors.toList() );
        }
        if( bankName != null ) {
            List<BankDto> midDto = bankService.getBanksByNameContaining( bankName );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( wilayaId != null ) {
            List<BankDto> midDto = bankService.getBanksByWilaya( wilayaId );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( bankCode != null ) {
            List<BankDto> midDto = bankService.getBanksByCodeContaining( bankCode );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( maxLicense != null ) {
            List<BankDto> midDto = bankService.getBanksByTotalLicenseLesserOrEqualThan( maxLicense );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( minLicense != null ) {
            List<BankDto> midDto = bankService.getBanksByTotalLicenseGreaterOrEqualThan( minLicense );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( address != null ) {
            List<BankDto> midDto = bankService.getBanksByAddressContaining( address );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }
        if( phone != null ) {
            List<BankDto> midDto = bankService.getBanksByPhoneContaining( phone );
            finalDto = finalDto.stream().distinct().filter( midDto::contains ).collect( Collectors.toList() );
        }

        return ResponseEntity.ok( finalDto );
    }

    @GetMapping("page")
    public ResponseEntity<BankPageDto> getBanksByPage(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name= "size")
            @Parameter(description = "The size of the page") Integer size
    ) {
        return ResponseEntity.ok( bankService.getBanks( page, size ) );
    }

    @GetMapping("light")
    public ResponseEntity<List<BankLightDto>> getBanksLightFormat() {
        return ResponseEntity.ok( bankService.getBanksLightFormat() );
    }

    @GetMapping("{id}")
    public ResponseEntity<BankDto> getBank(@PathVariable("id") Long bankId){
        BankDto bankDto = bankService.getBank(bankId);
        return ResponseEntity.ok(bankDto);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<BankDto> updateBank(@RequestBody BankDto bankDto,
                                                      @PathVariable("id") Long bankId){
        return ResponseEntity.ok(bankService.updateBank(bankDto, bankId));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBank(@PathVariable("id") Long bankId){
        bankService.deleteBank(bankId);
        return ResponseEntity.ok("Bank deleted successfully.");
    }
}
