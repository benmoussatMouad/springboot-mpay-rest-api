package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Otp;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.payload.CheckOtpDto;
import com.springboot.mpaybackend.payload.ForgetPasswordCheckOtpDto;
import com.springboot.mpaybackend.repository.OtpRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.springboot.mpaybackend.utils.AppConstants.DEFAULT_OTP_EXPIRATION_TIME;
import static com.springboot.mpaybackend.utils.AppConstants.ONE_MINUTE_IN_MILLIS;

@Service
public class OtpServiceImpl implements OtpService {

    private OtpRepository otpRepository;
    private UserRepository userRepository;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
    }

    public Otp createOtp(User user) {

        Integer random = (int) ((Math.random() * (999999) + 0));

        Otp otp;
        if( otpRepository.existsByUser( user ) ) {
            otp = otpRepository.findByUser( user )
                    .orElseThrow( () ->
                            new ResourceNotFoundException( "Otp", "Id", user.getId() ) );

            otp.setCode( String.format( "%1$06d", random ) );
            otp.setUsed( false );
            otp.setExpired( false );
            otp.setExpiresAt(addMinutesToDate(DEFAULT_OTP_EXPIRATION_TIME, new Date()));
            otpRepository.save( otp );
        } else {

            otp = new Otp();
            otp.setUser( user );
            otp.setExpired( false );
            otp.setUsed( false );
            otp.setAttempts( 0 );
            otp.setCode( String.format( "%1$06d", random ) );
            otp.setCreatedAt( new Date() );
            otp.setExpiresAt(addMinutesToDate(DEFAULT_OTP_EXPIRATION_TIME, new Date()));
            otpRepository.save( otp );
        }

        return otp;
    }

    @Override
    public void sendOtpToUser(Long userId) {
        Otp otp = otpRepository.findByUserId( userId )
                .orElseThrow( () -> new ResourceNotFoundException( "Otp", "user id", userId ) );

        System.out.println( otp.getCode() );
    }

    @Override
    public void createOtp() {

    }

    @Override
    public Boolean checkOtp(CheckOtpDto dto) {
        User user = userRepository.findByUsername( dto.getUsername() )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", dto.getUsername() ) );

        Otp otp = otpRepository.findByUser( user )
                .orElseThrow( () -> new ResourceNotFoundException( "Otp", "User's username", dto.getUsername() ) );
        otp.setAttempts( otp.getAttempts() + 1 );
        if( otp.getAttempts() > 5 ) {
            // TODO: Fraude management
        }

        if( otp.isUsed() || otp.isExpired() ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "OTP is used or expired. Resend new one" );
        }
        if( otp.getCode().equals( dto.getOtp() ) ) {
            otp.setUsed( true );
            otpRepository.save( otp );
            return true;
        } else {
            otp.increaseAttempt();
            return false;
        }
    }

    @Override
    public Boolean checkOtp(ForgetPasswordCheckOtpDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Otp otp = otpRepository.findByUser( user )
                .orElseThrow( () -> new ResourceNotFoundException( "Otp", "User's username", username ) );
        otp.setAttempts( otp.getAttempts() + 1 );
        if( otp.getAttempts() > 5 ) {
            // TODO: Fraude management
        }

        if( otp.isUsed() || otp.isExpired() ) {
            throw new MPayAPIException( HttpStatus.FORBIDDEN, "OTP is used or expired. Resend new one" );
        }
        if( otp.getCode().equals( dto.getOtp() ) ) {
            otp.setUsed( true );
            otpRepository.save( otp );
            return true;
        } else {
            return false;
        }
    }

    private Date addMinutesToDate(int minutes, Date beforeTime) {

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs
                + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }
}
