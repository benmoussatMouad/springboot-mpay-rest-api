package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.payload.UserAgencyDto;

import java.util.List;

public interface UserAgencyService {

    UserAgencyDto getUserAgency(Long id);

    UserAgencyDto getUserAgencyByUsername(String username);

    List<UserAgencyDto> getUsersAgencyByAgency(Long agencyId);

    UserAgencyDto addUserAgency(UserAgencyDto dto, String username);

    List<UserAgencyDto> getUsersAgency();

    UserAgencyDto updateUserAgency(UserAgencyDto dto, Long id, String updatingUsername);

    void deleteUserAgency(Long id);
}