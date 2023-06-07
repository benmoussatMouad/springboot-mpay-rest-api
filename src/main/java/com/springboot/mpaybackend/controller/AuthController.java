package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.payload.JWTAuthResponse;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.RegisterDto;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.AuthService;
import com.springboot.mpaybackend.service.DeviceHistoryService;
import com.springboot.mpaybackend.service.OtpService;
import com.springboot.mpaybackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;
    private UserRepository userRepository;
    private OtpService otpService;
    private DeviceHistoryService deviceHistoryService;
    private UserService userService;

    public AuthController(AuthService authService, UserRepository userRepository, OtpService otpService, DeviceHistoryService deviceHistoryService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.deviceHistoryService = deviceHistoryService;
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
    public ResponseEntity<String> registerClient(@RequestBody RegisterDto registerDto){
        String response = authService.registerClient(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Merchant Sign up
    @PostMapping({"/register/merchant", "/signup/merchant"})
    public ResponseEntity<String> registerMerchant(@RequestBody RegisterDto dto) {

        String response = authService.registerMerchant( dto );

        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    @PostMapping("/otp/send/{username}")
    public ResponseEntity<String> sendOtp(@PathVariable String username) {
        User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", username ) );
        otpService.createOtp( user );
        otpService.sendOtpToUser( user.getId() );

        return ResponseEntity.ok("Otp sent successfully");
    }

    @PostMapping("/otp/check")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<String> checkOtp(@RequestBody CheckOtpDto dto) {
        if( otpService.checkOtp( dto ) ) {
            deviceHistoryService.addDeviceHistory( dto );
            userService.enableUserByUsername( dto.getUsername() );
            return ResponseEntity.ok( "Otp checked successfully" );
        } else {
            return ResponseEntity.status( HttpStatus.FORBIDDEN ).body( "Otp is wrong" );
        }

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