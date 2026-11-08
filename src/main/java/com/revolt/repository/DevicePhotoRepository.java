package com.revolt.repository;

import com.revolt.model.DevicePhoto;
import com.revolt.model.PhotoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevicePhotoRepository extends JpaRepository<DevicePhoto, Long> {
    List<DevicePhoto> findByTradeInDeviceId(Long tradeInDeviceId);
    List<DevicePhoto> findByTradeInDeviceIdAndPhotoType(Long tradeInDeviceId, PhotoType photoType);
}
