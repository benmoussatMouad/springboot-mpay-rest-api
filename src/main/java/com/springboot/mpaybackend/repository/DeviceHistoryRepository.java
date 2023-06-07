package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {
}
