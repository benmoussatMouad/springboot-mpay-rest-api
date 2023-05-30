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
            @Parameter(description = "The size of the page") Integer size,
            @RequestParam(name= "name", required = false)
            @Parameter(description = "Filter the results by name containing") String name,
            @RequestParam(name= "phone", required = false)
            @Parameter(description = "Filter the results by phone containing") String phone,
            @RequestParam(name= "user_type", required = false)
            @Parameter(description = "Filter the results by user type") String userType,
            @RequestParam(name= "bank_id", required = false)
            @Parameter(description = "The size of the page") Long bankId,
            @RequestParam(name= "agency_id", required = false)
            @Parameter(description = "The size of the page") Long agencyId

    ) {
        return ResponseEntity.ok( userAgencyService.getAllUserAgencyByFilter( page, size, name, phone, userType, bankId, agencyId ) );
    }

    @GetMapping("{key}")
    public ResponseEntity<UserAgencyDto> getUserAgencyBy(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

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

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserAgency(@PathVariable("id") Long id) {
        userAgencyService.deleteUserAgency( id );

        return ResponseEntity.ok("Agency User deleted successfully");
    }
}
