package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.BankDto;
import com.springboot.mpaybackend.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    //TODO: Get all banks in light form

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BankDto> addBank(@RequestBody BankDto bankDto){
        BankDto savedBank = bankService.addBank(bankDto);
        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<BankDto>> getBanks(){
        return ResponseEntity.ok(bankService.getBanks());
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
