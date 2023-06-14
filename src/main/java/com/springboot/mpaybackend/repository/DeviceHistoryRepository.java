package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {
    Boolean existsByDevice(String device);

    Optional<DeviceHistory> findByDevice(String device);
}
