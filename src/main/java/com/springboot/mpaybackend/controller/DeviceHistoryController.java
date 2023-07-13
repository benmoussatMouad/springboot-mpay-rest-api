package com.springboot.mpaybackend.controller;

import com.springboot.mpaybackend.payload.DeviceHistoryDto;
import com.springboot.mpaybackend.service.DeviceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok( deviceHistoryService.getDeviceHistoryByUsername( authentication.getName() ) );
    }

    @DeleteMapping("/me/{device-id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT', 'MERCHANT')")
    public ResponseEntity<String> deleteOneHistory(Authentication authentication, @PathVariable("device-id") Long id) {
        deviceHistoryService.deleteDeviceHistory( authentication.getName(), id );

        return ResponseEntity.ok("Device history deleted succesfully");
    }
}
