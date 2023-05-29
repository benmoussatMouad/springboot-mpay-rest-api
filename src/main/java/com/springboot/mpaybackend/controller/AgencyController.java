package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.Agency;
import com.springboot.mpaybackend.payload.AgencyDto;
import com.springboot.mpaybackend.payload.AgencyLightDto;
import com.springboot.mpaybackend.payload.AgencyResponseDto;
import com.springboot.mpaybackend.service.AgencyService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/agency")
public class AgencyController {
    private AgencyService agencyService;

    public AgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @PostMapping
    public ResponseEntity<AgencyDto> addAgency(@RequestBody AgencyDto agencyDto) {

        AgencyDto savedAgency = agencyService.addAgency( agencyDto );
        return new ResponseEntity<>( savedAgency, HttpStatus.CREATED );
    }

    @GetMapping
    public ResponseEntity<List<AgencyResponseDto>> getAgencies(
            @RequestParam(name = "bank_id",required = false) @Parameter(description = "if this is not null, this will filter the result by bank id", example = "1") Long bankId,
            @RequestParam(name = "wilaya_id",required = false) @Parameter(description = "if this is not null, this will filter the result by wilaya id", example = "1") Long wilayaId,
            @RequestParam(name = "agency_code",required = false) @Parameter(description = "if this is not null, this will filter the result to agencies containing this code in their codename", example = "BE, returns BEA and others") String agencyCode,
            @RequestParam(name = "phone",required = false) @Parameter(description = "if this is not null, this will filter the result to agencies containing this phone in their phone number", example = "558 might return qn agency with phone number 0558394565") String phone,
            @RequestParam(name = "agency_name",required = false) @Parameter(description = "if this is not null, this will filter the result by agencies containing the name", example = "Nationale return Banque Nationale and others") String agencyName
    ) {
        List<AgencyResponseDto> finalAgencyDto = agencyService.getAgencies();

        if( bankId != null ) {
            List<AgencyResponseDto>  midResponse = agencyService.getAgenciesByBank( bankId );
            finalAgencyDto = finalAgencyDto.stream().distinct().filter( midResponse::contains ).collect( Collectors.toList() );
        }
        if( wilayaId != null ) {
            List<AgencyResponseDto> midResponse = agencyService.getAgenciesByWilaya( wilayaId );
            finalAgencyDto = finalAgencyDto.stream().distinct().filter( midResponse::contains ).collect( Collectors.toList() );
        }
        if( agencyName != null ) {
            List<AgencyResponseDto> midResponse = agencyService.getAgenciesByNameContaining( agencyName );
            finalAgencyDto = finalAgencyDto.stream().distinct().filter( midResponse::contains ).collect( Collectors.toList() );
        }
        if( agencyCode != null ) {
            List<AgencyResponseDto> midResponse = agencyService.getAgenciesByCodeContaining( agencyCode );
            finalAgencyDto = finalAgencyDto.stream().distinct().filter( midResponse::contains ).collect( Collectors.toList() );
        }
        if( phone != null ) {
            List<AgencyResponseDto> midResponse = agencyService.getAgenciesByPhoneContaining( phone );
            finalAgencyDto = finalAgencyDto.stream().distinct().filter( midResponse::contains ).collect( Collectors.toList() );
          }
        return ResponseEntity.ok( finalAgencyDto );
    }

    @GetMapping("light")
    public ResponseEntity<List<AgencyLightDto>> getAgenciesLightFormat() {
        return ResponseEntity.ok( agencyService.getAgenciesLightFormat() );
    }

    @GetMapping("{id}")
    public ResponseEntity<AgencyDto> getAgency(@PathVariable Long id) {
        AgencyDto agencyDto = agencyService.getAgency( id );

        return ResponseEntity.ok( agencyDto );
    }

    @PutMapping("{id}")
    public ResponseEntity<AgencyDto> updateAgency(
            @PathVariable("id") Long agencyId,
            @RequestBody AgencyDto agencyDto) {

        return ResponseEntity.ok( agencyService.updateAgency( agencyDto, agencyId ) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAgency(@PathVariable("id") Long agencyId) {
        agencyService.deleteAgency( agencyId );

        return ResponseEntity.ok("Agency deleted successfully");
    }
}
