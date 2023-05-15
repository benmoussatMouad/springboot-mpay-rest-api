package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.User;
import com.springboot.mpaybackend.payload.UserAdminDto;

import java.util.List;

public interface UserAdminService {

    UserAdminDto getUserAdmin(Long id);

    UserAdminDto getUserAdminByUsername(String username);

    List<UserAdminDto> getAllUserAdmin();

    UserAdminDto updateUserAdmin(UserAdminDto dto, Long id);

    UserAdminDto addUserAdmin(UserAdminDto dto);

    void deleteUserAdmin(Long id);
}
