package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.entity.Merchant;
import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.entity.UserType;
import com.springboot.mpaybackend.exception.ResourceNotFoundException;
import com.springboot.mpaybackend.repository.MerchantRepository;
import com.springboot.mpaybackend.repository.UserRepository;
import com.springboot.mpaybackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    MerchantRepository merchantRepository;

    public UserServiceImpl(UserRepository userRepository, MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void enableUserByUsername(String username) {
        User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new ResourceNotFoundException( "User", "username", username ) );

        user.setEnabled( true );
        userRepository.save( user );

        if( user.getUserType().equals( UserType.MERCHANT ) ) {
            Merchant merchant = merchantRepository.findByUsernameUsernameAndDeletedFalse( username )
                    .orElseThrow( () -> new ResourceNotFoundException( "Merchant", "username", username ) );

            merchant.setEnabled( true );
            merchantRepository.save( merchant );
        } //TODO: Treat other cases
    }
}
