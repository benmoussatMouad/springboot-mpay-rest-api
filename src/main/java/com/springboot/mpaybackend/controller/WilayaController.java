package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.payload.WilayaDto;
import com.springboot.mpaybackend.service.WilayaService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wilaya")
public class WilayaController {

    WilayaService wilayaService;

    public WilayaController(WilayaService wilayaService) {
        this.wilayaService = wilayaService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WilayaDto> addBank(@RequestBody WilayaDto dto){
        WilayaDto savedWilaya = wilayaService.addWilaya(dto);
        return new ResponseEntity<>(savedWilaya, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<WilayaDto>> getBanks(){
        return ResponseEntity.ok(wilayaService.getAllWilayas());
    }

    @GetMapping("{id}")
    public ResponseEntity<WilayaDto> getWilaya(@PathVariable("id")  Long wilayaId){
        WilayaDto wilayaDto = wilayaService.getWilaya(wilayaId);
        return ResponseEntity.ok(wilayaDto);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<WilayaDto> updateBank(@RequestBody WilayaDto dto,
                                              @PathVariable("id") Long id){
        return ResponseEntity.ok(wilayaService.updateWilaya(dto, id));
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBank(@PathVariable("id") Long id){
        wilayaService.deleteWilaya(id);
        return ResponseEntity.ok("Wilaya deleted successfully.");
    }
}

