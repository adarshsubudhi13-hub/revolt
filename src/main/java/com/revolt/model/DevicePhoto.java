package com.revolt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_photos")
public class DevicePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tradeInDeviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhotoType photoType;

    @Column(nullable = false, length = 1000)
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public DevicePhoto() {}

    public DevicePhoto(Long tradeInDeviceId, PhotoType photoType, String fileUrl) {
        this.tradeInDeviceId = tradeInDeviceId;
        this.photoType = photoType;
        this.fileUrl = fileUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTradeInDeviceId() { return tradeInDeviceId; }
    public void setTradeInDeviceId(Long tradeInDeviceId) { this.tradeInDeviceId = tradeInDeviceId; }

    public PhotoType getPhotoType() { return photoType; }
    public void setPhotoType(PhotoType photoType) { this.photoType = photoType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
