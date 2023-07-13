package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {
    Boolean existsByDevice(String device);

    List<DeviceHistory> findByDevice(String device);

    List<DeviceHistory> findByUsernameUsernameAndDeletedFalse(String name);
}
