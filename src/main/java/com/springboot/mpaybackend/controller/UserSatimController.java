package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.*;
import com.springboot.mpaybackend.service.UserSatimService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user-satim")
public class UserSatimController {


    UserSatimService userSatimService;

    public UserSatimController(UserSatimService userSatimService) {
        this.userSatimService = userSatimService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @PreAuthorize( "#username == authentication.getName()" )
    public ResponseEntity<UserSatimDto> addUser(@RequestBody UserSatimDto dto, Authentication authentication) {

        UserSatimDto savedUserAgency = userSatimService.addUser( dto, authentication.getName() );
        return new ResponseEntity<>( savedUserAgency, HttpStatus.CREATED );
    }


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserSatimDto>> getUsers(
    ) {
        return ResponseEntity.ok( userSatimService.getAllUsers() );
    }

    @GetMapping("page")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserSatimPageDto> getUsersByPage(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name= "size")
            @Parameter(description = "The size of the page") Integer size,
            @RequestParam(name= "name", required = false)
            @Parameter(description = "Filter the results by name containing") String name,
            @RequestParam(name= "phone", required = false)
            @Parameter(description = "Filter the results by phone containing") String phone,
            @RequestParam(name= "id", required = false)
            @Parameter(description = "Id of the bank user") Long id
    ) {


        return ResponseEntity.ok( userSatimService.getAllUsersByFilter(id, page, size, name, phone) );
    }


    @GetMapping("{key}")
    public ResponseEntity<UserSatimDto> getUserAgencyBy(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok( userSatimService.getUser( id ) );
            case "username":
                return ResponseEntity.ok( userSatimService.getUserByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SATIM')")
    public ResponseEntity<UserSatimDto> updateUser(@RequestBody UserSatimDto dto,
                                                        @PathVariable Long id,
                                                        Authentication authentication) {

        return ResponseEntity.ok( userSatimService.updateUser( dto, id, authentication.getName() ) );
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SATIM')")
    public ResponseEntity<String> deleteUserBank(@PathVariable("id") Long id) {
        userSatimService.deleteUserBank( id );

        return ResponseEntity.ok("Satim User deleted successfully");
    }
}
