package com.revolt.repository;

import com.revolt.model.TradeInDevice;
import com.revolt.model.TradeInStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeInDeviceRepository extends JpaRepository<TradeInDevice, Long> {
    List<TradeInDevice> findByUserId(Long userId);
    List<TradeInDevice> findByUserIdAndStatus(Long userId, TradeInStatus status);
    Optional<TradeInDevice> findByImei(String imei);
    boolean existsByImeiAndStatusNot(String imei, TradeInStatus status);
}
