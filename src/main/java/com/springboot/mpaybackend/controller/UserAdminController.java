package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.UserAdminDto;
import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.service.UserAdminService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-admin")
public class UserAdminController {

    UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize( "#username == authentication.getName()" )
    public ResponseEntity<UserAdminDto> addUserAdmin(@RequestBody UserAdminDto dto, Authentication authentication) {

        UserAdminDto savedAdmin = userAdminService.addUserAdmin( dto );
        return new ResponseEntity<>( savedAdmin, HttpStatus.CREATED );
    }

    @GetMapping
    public ResponseEntity<List<UserAdminDto>> getUsersAdmin() {

        return ResponseEntity.ok( userAdminService.getAllUserAdmin() );
    }

    @GetMapping("{key}")
    public ResponseEntity<UserAdminDto> getUserAdminBy(
            @PathVariable("key") String key,
            @RequestParam(name = "by", defaultValue = "id") @Parameter(description = "Specify by which key to get the User", example = "by=id OR by=username") String filter) {

        switch (filter) {
            case "id":
                Long id = Long.valueOf( key );
                return ResponseEntity.ok(userAdminService.getUserAdmin( id ));
            case "username":
                return ResponseEntity.ok( userAdminService.getUserAdminByUsername( key ) );
            default:
                throw new IllegalStateException( "Unexpected value: " + filter );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<UserAdminDto> updateUserAdmin(@RequestBody UserAdminDto dto,
                                                          @PathVariable Long id,
                                                          Authentication authentication) {

        return ResponseEntity.ok( userAdminService.updateUserAdmin( dto, id) );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserAdmin(@PathVariable("id") Long id) {
        userAdminService.deleteUserAdmin( id );

        return ResponseEntity.ok("Agency User deleted successfully");
    }
}
