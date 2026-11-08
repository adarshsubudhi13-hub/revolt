package com.revolt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_in_devices")
public class TradeInDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category; // e.g., Smartphone, Laptop, Tablet, Smartwatch

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(columnDefinition = "TEXT")
    private String specsJson; // RAM, Storage, Processor, etc.

    @Column(nullable = false)
    private Integer ageMonths = 12;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionGrade conditionGrade;

    @Column(nullable = false)
    private String imei;

    @Column(nullable = false)
    private Boolean hasOriginalBox = false;

    @Column(nullable = false)
    private Boolean hasOriginalCharger = false;

    @Column(nullable = false)
    private Boolean hasValidBill = false;

    @Column(nullable = false)
    private Long estimatedPoints;

    @Column(nullable = false)
    private LocalDateTime lockedUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeInStatus status = TradeInStatus.VALUATED;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TradeInDevice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSpecsJson() { return specsJson; }
    public void setSpecsJson(String specsJson) { this.specsJson = specsJson; }

    public Integer getAgeMonths() { return ageMonths; }
    public void setAgeMonths(Integer ageMonths) { this.ageMonths = ageMonths; }

    public ConditionGrade getConditionGrade() { return conditionGrade; }
    public void setConditionGrade(ConditionGrade conditionGrade) { this.conditionGrade = conditionGrade; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public Boolean getHasOriginalBox() { return hasOriginalBox; }
    public void setHasOriginalBox(Boolean hasOriginalBox) { this.hasOriginalBox = hasOriginalBox; }

    public Boolean getHasOriginalCharger() { return hasOriginalCharger; }
    public void setHasOriginalCharger(Boolean hasOriginalCharger) { this.hasOriginalCharger = hasOriginalCharger; }

    public Boolean getHasValidBill() { return hasValidBill; }
    public void setHasValidBill(Boolean hasValidBill) { this.hasValidBill = hasValidBill; }

    public Long getEstimatedPoints() { return estimatedPoints; }
    public void setEstimatedPoints(Long estimatedPoints) { this.estimatedPoints = estimatedPoints; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }

    public TradeInStatus getStatus() { return status; }
    public void setStatus(TradeInStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
