package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.JWTAuthResponse;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.RegisterDto;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;
    private UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    // Build Login REST API
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        User user = userRepository.findByUsername( loginDto.getUsernameOrEmail() )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", 0 ) );
        jwtAuthResponse.setUser( user );

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // Build Register REST API
    @PostMapping(value = {"/register/client", "/signup/client"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.registerClient(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("check/username/{username}")
    public ResponseEntity<String> checkUsername(@PathVariable("username") String username) {
        Boolean response = authService.checkUsername( username );
        if( response ) {
            return new ResponseEntity<>( "Username exists", HttpStatus.ACCEPTED );
        } else {
            return new ResponseEntity<>( "Username do not exists", HttpStatus.NOT_FOUND );
        }
    }

    @GetMapping("check/phone/{phone}")
    public ResponseEntity<String> checkPhone(@PathVariable("phone") String phone) {
        Boolean response = authService.checkPhone( phone );
        if( response ) {    
            return new ResponseEntity<>( "Phone exists", HttpStatus.ACCEPTED );
        } else {
            return new ResponseEntity<>( "Phone do not exists", HttpStatus.NOT_FOUND );
        }
    }
}