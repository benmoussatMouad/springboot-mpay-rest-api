package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.UserType;
import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.UserBankService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user-bank")
public class UserBankController {

    private UserBankService userBankService;

    public UserBankController(UserBankService userBankService) {
        this.userBankService = userBankService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize( "#username == authentication.getName()" )
    public ResponseEntity<UserBankDto> addUser(@RequestBody UserBankDto dto, Authentication authentication) {

        System.out.println( authentication.getName() );
        UserBankDto savedUserAgency = userBankService.addUserBank( dto, authentication.getName() );
        return new ResponseEntity<>( savedUserAgency, HttpStatus.CREATED );
    }


    @GetMapping
    public ResponseEntity<List<UserBankDto>> getUsersAgency(
            @RequestParam(name = "by",  required = false) @Parameter(description = "specify condition to filter Bank Users", example = "bank") String filter,
            @RequestParam(name = "id", required = false) @Parameter(description = "If 'by' is specified as an associated entity, id should be included and should be the id of the associated entity", example = "id of an agency associated to agency user") String id
    ) {
        if( "bank".equals( filter ) ) {
            return ResponseEntity.ok( userBankService.getUsersBankByBankId( Long.valueOf( id ) ) );
        }
        return ResponseEntity.ok( userBankService.getAllUserBanks() );
    }

    @GetMapping("page")
    public ResponseEntity<UserBankPageDto> getBanksByPage(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name= "size")
            @Parameter(description = "The size of the page") Integer size,
            @RequestParam(name= "name", required = false)
            @Parameter(description = "Filter the results by name containing") String name,
            @RequestParam(name= "phone", required = false)
            @Parameter(description = "Filter the results by phone containing") String phone,
            @RequestParam(name= "user_type", required = false)
            @Parameter(description = "Filter the results by user type") String userType,
            @RequestParam(name= "bank_id", required = false)
            @Parameter(description = "The size of the page") Long bankId,
            @RequestParam(name= "id", required = false)
            @Parameter(description = "Id of the bank user") Long id
            ) {


        return ResponseEntity.ok( userBankService.getAllUserBankByFilter(id, page, size, name, phone, userType, bankId ) );
    }


    @GetMapping("{key}")
    public ResponseEntity<UserBankResponseDto> getUserAgencyBy(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok( userBankService.getUserBank( id ) );
            case "username":
                return ResponseEntity.ok( userBankService.getUserBankByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<UserBankDto> updateUserAgency(@RequestBody UserBankDto dto,
                                                          @PathVariable Long id,
                                                          Authentication authentication) {

        return ResponseEntity.ok( userBankService.updateUserBank( dto, id, authentication.getName() ) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserBank(@PathVariable("id") Long id) {
        userBankService.deleteUserBank( id );

        return ResponseEntity.ok("Bank User deleted successfully");
    }
}
