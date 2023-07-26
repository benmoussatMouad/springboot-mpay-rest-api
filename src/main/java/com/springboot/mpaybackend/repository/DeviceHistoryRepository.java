package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {
    Boolean existsByDeviceAndDeletedFalse(String device);

    List<DeviceHistory> findByDeviceAndDeletedFalse(String device);

    List<DeviceHistory> findByUsernameUsernameAndDeletedFalse(String name);

    Optional<DeviceHistory> findByIdAndDeletedFalse(Long id);
}
