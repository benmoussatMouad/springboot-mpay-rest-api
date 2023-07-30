package com.springboot.mpaybackend.service;


import com.springboot.mpaybackend.payload.UserSatimDto;
import com.springboot.mpaybackend.payload.UserSatimPageDto;

import java.util.List;

public interface UserSatimService {
    UserSatimDto addUser(UserSatimDto dto, String creatingUsername);

    List<UserSatimDto> getAllUsers();

    UserSatimPageDto getAllUsersByFilter(Long id, Integer page, Integer size, String name, String phone);

    UserSatimDto getUser(Long id);

    UserSatimDto getUserByUsername(String username);

    UserSatimDto updateUser(UserSatimDto dto, Long id, String updatingUser);

    void deleteUserBank(Long id);
}
