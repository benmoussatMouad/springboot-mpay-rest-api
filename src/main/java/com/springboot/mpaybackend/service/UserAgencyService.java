package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.UserAgencyDto;
import com.springboot.mpaybackend.payload.UserAgencyPageDto;
import com.springboot.mpaybackend.payload.UserAgencyResponseDto;

import java.util.List;

public interface UserAgencyService {

    UserAgencyResponseDto getUserAgency(Long id);

    UserAgencyResponseDto getUserAgencyByUsername(String username);

    List<UserAgencyResponseDto> getUsersAgencyByAgency(Long agencyId);

    UserAgencyDto addUserAgency(UserAgencyDto dto, String username);

    List<UserAgencyResponseDto> getUsersAgency();

    UserAgencyResponseDto updateUserAgency(UserAgencyDto dto, Long id, String updatingUsername);
    //TODO: change it to logical suppression
    void deleteUserAgency(Long id);

    UserAgencyPageDto getAllUserAgency(Integer page, Integer size);

    UserAgencyPageDto getAllUserAgencyByFilter(Long id, Integer page, Integer size, String name, String phone, String userType, Long bankId, Long agencyId);

    boolean existsById(Long id);
}
