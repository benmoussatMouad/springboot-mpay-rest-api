package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.RegisterDto;
import com.springboot.mpaybackend.repository.*;
import com.springboot.mpaybackend.security.JwtTokenProvider;
import com.springboot.mpaybackend.service.AuthService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private ClientRepository clientRepository;
    private RoleRepository roleRepository;
    private OtpRepository otpRepository;
    private WilayaRepository wilayaRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private MerchantRepository merchantRepository;
    private ModelMapper modelMapper;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           ClientRepository clientRepository, RoleRepository roleRepository,
                           OtpRepository otpRepository, WilayaRepository wilayaRepository, PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, MerchantRepository merchantRepository, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
        this.otpRepository = otpRepository;
        this.wilayaRepository = wilayaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.merchantRepository = merchantRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {

        // add check for username exists in database
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully!.";
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public String registerClient(RegisterDto registerDto) {

        // check if Client exists by phone
        if(clientRepository.existsByPhone( registerDto.getPhone() ) ) {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Phone already exists!.");
        }

        // check if username is taken by other users
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Username already exists!.");
        }


        // 1-Create a user
        User user = new User();
        user.setUsername( registerDto.getUsername() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );
        user.setPhone( registerDto.getPhone() );
        user.setFirstConnexion( true );
        user.setUserType( UserType.CLIENT );
        userRepository.save( user );

        // 1.2- Create OTP and send it

        //createOtp( user );

        // 2-Create a client
        Client client = new Client();
        client.setUsername( user );
        client.setFirstName( registerDto.getFirstName() );
        client.setLastName( registerDto.getLastName() );
        client.setAddress( registerDto.getAddress() );

        Wilaya wilaya = wilayaRepository.findById(registerDto.getWilayaId())
                .orElseThrow(() -> new ResourceNotFoundException("Wilaya", "id", registerDto.getWilayaId()));
        client.setWilaya( wilaya );

        client.setPhone( registerDto.getPhone() );
        client.setCommune( registerDto.getCommune() );

        // 3- Save both, if failure don't
        clientRepository.save( client );

        return "User client registered successfully!.";
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public String registerMerchant(RegisterDto dto) {
        // check if merchant exists by phone
        if(merchantRepository.existsByPhone( dto.getPhone() ) ) {
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Phone already exists!.");
        }

        // check if username is taken by other users
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new MPayAPIException(HttpStatus.BAD_REQUEST, "Username already exists!.");
        }

        // *** 1- Creating User
        // ***
        User user = new User();
        user.setUsername( dto.getUsername() );
        user.setPassword( passwordEncoder.encode( dto.getPassword() ) );
        user.setPhone( dto.getPhone() );
        user.setFirstConnexion( true );
        user.setUserType( UserType.CLIENT );
        user.setEnabled( false );
        user.setSuffersAttempts( 0 );
        userRepository.save( user );
        // ***

        // *** 2- Creating Merchant
        // ***
        Merchant merchant = modelMapper.map( dto, Merchant.class );
        merchant.setUsername( user );
        merchant.setStatus( MerchantStatus.NON_VERIFIED );
        merchant.setEnabled( false );
        merchantRepository.save( merchant );

        return "Merchant registered successfully";
    }

    @Override
    public Boolean checkUsername(String username) {
        return userRepository.existsByUsername( username );
    }

    @Override
    public Boolean checkPhone(String phone) {
        return userRepository.existsByPhone( phone );
    }

    @Override
    public Boolean checkClientPhone(String phone) {
        return clientRepository.existsByPhone( phone );
    }

    @Override
    public Boolean checkMerchantPhone(String phone) {
        return merchantRepository.existsByPhone( phone );
    }
}
