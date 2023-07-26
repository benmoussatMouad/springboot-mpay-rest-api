package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.*;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.ActorLoginDto;
import com.springboot.mpaybackend.payload.LoginDto;
import com.springboot.mpaybackend.payload.PasswordChangeDto;
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
import java.util.List;
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
    private DeviceHistoryRepository deviceHistoryRepository;
    private MerchantStatusTraceRepository merchantStatusTraceRepository;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           ClientRepository clientRepository, RoleRepository roleRepository,
                           OtpRepository otpRepository, WilayaRepository wilayaRepository, PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, MerchantRepository merchantRepository, ModelMapper modelMapper, DeviceHistoryRepository deviceHistoryRepository, MerchantStatusTraceRepository merchantStatusTraceRepository) {
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
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.merchantStatusTraceRepository = merchantStatusTraceRepository;
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
        if(clientRepository.existsByPhoneAndDeletedFalse( registerDto.getPhone() ) ) {
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

        // 2-Create a client
        Client client = new Client();
        client.setUser( user );
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
        user.setUserType( UserType.MERCHANT );
        user.setEnabled( false );
        user.setSuffersAttempts( 0 );
        userRepository.save( user );
        // ***

        // *** 2- Creating Merchant
        // ***
        Merchant merchant = modelMapper.map( dto, Merchant.class );
        //FIXME
        merchant.setId( null );
        merchant.setUsername( user );
        merchant.setStatus( MerchantStatus.NON_VERIFIED );
        merchant.setEnabled( false );
        merchantRepository.save( merchant );

        // *** 3- Saving first trace
        // ***
        MerchantStatusTrace trace = new MerchantStatusTrace();
        trace.setMerchant( merchant );
        trace.setUser( user );
        trace.setCreatedAt( new Date() );
        trace.setStatus( MerchantStatus.NON_VERIFIED );
        merchantStatusTraceRepository.save(trace);

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
        return clientRepository.existsByPhoneAndDeletedFalse( phone );
    }

    @Override
    public Boolean checkMerchantPhone(String phone) {
        return merchantRepository.existsByPhone( phone );
    }

    @Override
    public Boolean verifyMerchantLogin(ActorLoginDto dto) {

        if( !merchantRepository.existsByUsernameUsername( dto.getUsernameOrEmail() ) ) {
            throw new ResourceNotFoundException( "Merchant", "username", dto.getUsernameOrEmail() );
        }

        if( !deviceHistoryRepository.existsByDeviceAndDeletedFalse( dto.getDevice() ) ) {
            return false;
        } else {
            List<DeviceHistory> deviceHistories = deviceHistoryRepository.findByDeviceAndDeletedFalse( dto.getDevice() );

            // TODO: Do checks for when to ask for a new OTP

            deviceHistories.stream().filter( d -> d.getWilaya().getNumber().equals( dto.getWilayaNumber() ) ).findAny().orElse( null );

            // Check if the device is for the corresponding user
            return deviceHistories.stream().map( e -> e.getUsername().getUsername() ).toList().contains( dto.getUsernameOrEmail() );
        }
    }

    @Override
    public Boolean verifyClientLogin(ActorLoginDto dto) {
        if( !clientRepository.existsByUserUsernameAndDeletedFalse( dto.getUsernameOrEmail() ) ) {
            throw new ResourceNotFoundException( "Client", "username", dto.getUsernameOrEmail() );
        }

        if( !deviceHistoryRepository.existsByDeviceAndDeletedFalse( dto.getDevice() ) ) {
            return false;
        } else {
            List<DeviceHistory> deviceHistory = deviceHistoryRepository.findByDeviceAndDeletedFalse( dto.getDevice() );

            // TODO: Do checks for when to ask for a new OTP
            // Check if the device is for the corresponding user
            return deviceHistory.stream().map( e -> e.getUsername().getUsername() ).toList().contains( dto.getUsernameOrEmail() );
        }
    }

    @Override
    public Boolean changePassword(String username, PasswordChangeDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if the old password is correct
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Old password doesn't match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        return true;
    }

    @Override
    public void setNewPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Otp otp = otpRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Otp", "user", user.getUsername()));

        if (otp.isUsed()) { // Verify if expired
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new MPayAPIException(HttpStatus.FORBIDDEN, "Device should be verified");
        }
    }
}
