package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.AgencyDto;
import com.springboot.mpaybackend.payload.AgencyLightDto;
import com.springboot.mpaybackend.service.AgencyService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<AgencyDto>> getAgencies(
            @RequestParam(name = "by", required = false) @Parameter(description = "Describe how to filter agencies", example = "bank") String filter,
            @RequestParam(name = "bank_id",required = false) @Parameter(description = "If filter is set to 'bank', bank_id must be included", example = "1") Long bankId
    ) {

        if( "bank".equals( filter ) ) {
            return ResponseEntity.ok( agencyService.getAgenciesByBank( bankId ) );
        }
        return ResponseEntity.ok( agencyService.getAgencies() );
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
