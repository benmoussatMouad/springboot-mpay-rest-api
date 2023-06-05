package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.MerchantAccountService;
import com.springboot.mpaybackend.service.MerchantService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("api/v1/merchant")
public class MerchantController {
    private static final Logger logger = LoggerFactory.getLogger( MerchantController.class );


    private MerchantService merchantService;
    private MerchantAccountService merchantAccountService;

    public MerchantController(MerchantService merchantService, MerchantAccountService merchantAccountService) {
        this.merchantService = merchantService;
        this.merchantAccountService = merchantAccountService;
    }

    @PostMapping
    public ResponseEntity<MerchantResponseDto> createMerchant(@RequestBody MerchantDto dto) {

        return ResponseEntity.ok( merchantService.addMerchant( dto, false ) );
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponseDto>> getUsersAgency() {

        return ResponseEntity.ok( merchantService.getAllMerchants() );
    }

    @GetMapping("trace/{merchantId}")
    public ResponseEntity<List<MerchantAccountTraceDto>> getMerchantTraces(@PathVariable("merchantId") Long id) {

        return ResponseEntity.ok( merchantAccountService.getAllMerchantStatusTraces( id ) );
    }

    // TODO: Actions for changing the merchant status, when to create an account if the merchant is not directly created by a bank user

    @GetMapping("page")
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
            @Parameter(description = "The id of merchant within the table") Long id
    ) {
        System.out.println( name );
        try {
            System.out.println( phone );
            return ResponseEntity.ok( merchantService.getAllMerchantsByFilter( page, size, id, name, name, phone, regCommerce, nif, status ) );
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
}

