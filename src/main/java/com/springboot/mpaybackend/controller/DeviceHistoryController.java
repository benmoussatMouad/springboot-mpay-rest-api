package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.DeviceHistoryDto;
import com.springboot.mpaybackend.service.DeviceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/device-history")
public class DeviceHistoryController {

    private DeviceHistoryService deviceHistoryService;

    public DeviceHistoryController(DeviceHistoryService deviceHistoryService) {
        this.deviceHistoryService = deviceHistoryService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT', 'MERCHANT')")
    public ResponseEntity<List<DeviceHistoryDto>> getMyHistory(Authentication authentication) {
        return ResponseEntity.ok(deviceHistoryService.getDeviceHistoryByUsername( authentication.getName() ));
    }
}
