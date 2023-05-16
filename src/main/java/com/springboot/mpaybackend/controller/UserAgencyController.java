package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.payload.UserAgencyPageDto;
import com.springboot.mpaybackend.service.UserAgencyService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-agency")
public class UserAgencyController {

    private UserAgencyService userAgencyService;

    public UserAgencyController(UserAgencyService userAgencyService) {
        this.userAgencyService = userAgencyService;
    }


    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize( "#username == authentication.getName()" )
    public ResponseEntity<UserAgencyDto> addUser(@RequestBody UserAgencyDto dto, Authentication authentication) {

        System.out.println( authentication.getName() );
        UserAgencyDto savedUserAgency = userAgencyService.addUserAgency( dto, authentication.getName() );
        return new ResponseEntity<>( savedUserAgency, HttpStatus.CREATED );
    }

    @GetMapping
    public ResponseEntity<List<UserAgencyDto>> getUsersAgency(
            @RequestParam(name = "by", required = false) @Parameter(description = "specify condition to filter Agency Users", example = "agency") String filter,
            @RequestParam(name = "id", required = false) @Parameter(description = "If 'by' is specified as associated entity, id should be included and should be the id of the associated entity", example = "id of an agency associated to agency user") String id
    ) {
        if( "agency".equals( filter ) ) {
            return ResponseEntity.ok( userAgencyService.getUsersAgencyByAgency( Long.valueOf( id ) ) );
        }
        return ResponseEntity.ok( userAgencyService.getUsersAgency() );
    }

    @GetMapping("page")
    public ResponseEntity<UserAgencyPageDto> getUserAgencyByPage(
            @RequestParam(name = "page")
            @Parameter(description = "The number of the desired page, start from 0") Integer page,
            @RequestParam(name= "size")
            @Parameter(description = "The size of the page") Integer size
    ) {
        return ResponseEntity.ok( userAgencyService.getAllUserAgency( page, size ) );
    }

    @GetMapping("{key}")
    public ResponseEntity<UserAgencyDto> getUserAgencyBy(
            @PathVariable("key") String key,
            @RequestParam(name = "by") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok(userAgencyService.getUserAgency( id ));
            case "username":
                return ResponseEntity.ok( userAgencyService.getUserAgencyByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<UserAgencyDto> updateUserAgency(@RequestBody UserAgencyDto dto,
                                                          @PathVariable Long id,
                                                          Authentication authentication) {

        return ResponseEntity.ok( userAgencyService.updateUserAgency( dto, id, authentication.getName() ) );
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUserAgency(Long id) {
        userAgencyService.deleteUserAgency( id );

        return ResponseEntity.ok("Agency User deleted successfully");
    }
}
