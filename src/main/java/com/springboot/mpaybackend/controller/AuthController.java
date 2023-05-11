package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.JWTAuthResponse;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.RegisterDto;
import com.springboot.mpaybackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto){
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // Build Register REST API
    @PostMapping(value = {"/register/client", "/signup/client"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.registerClient(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}