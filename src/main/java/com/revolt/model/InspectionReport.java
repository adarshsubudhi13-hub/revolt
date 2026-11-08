package com.revolt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_reports")
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long tradeInDeviceId;

    @Column(nullable = false)
    private Long agentId;

    @Column(nullable = false)
    private Boolean imeiMatched;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionGrade physicalGrade;

    @Column(nullable = false)
    private Boolean isApproved;

    private String mismatchReason;

    private Long revisedPoints;

    private Double additionalCashCollected = 0.0;

    @Column(columnDefinition = "TEXT")
    private String agentNotes;

    @Column(columnDefinition = "TEXT")
    private String agentPhotosJson; // Array of agent inspection photo URLs

    @Column(nullable = false)
    private LocalDateTime inspectedAt = LocalDateTime.now();

    public InspectionReport() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getTradeInDeviceId() { return tradeInDeviceId; }
    public void setTradeInDeviceId(Long tradeInDeviceId) { this.tradeInDeviceId = tradeInDeviceId; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Boolean getImeiMatched() { return imeiMatched; }
    public void setImeiMatched(Boolean imeiMatched) { this.imeiMatched = imeiMatched; }

    public ConditionGrade getPhysicalGrade() { return physicalGrade; }
    public void setPhysicalGrade(ConditionGrade physicalGrade) { this.physicalGrade = physicalGrade; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public String getMismatchReason() { return mismatchReason; }
    public void setMismatchReason(String mismatchReason) { this.mismatchReason = mismatchReason; }

    public Long getRevisedPoints() { return revisedPoints; }
    public void setRevisedPoints(Long revisedPoints) { this.revisedPoints = revisedPoints; }

    public Double getAdditionalCashCollected() { return additionalCashCollected; }
    public void setAdditionalCashCollected(Double additionalCashCollected) { this.additionalCashCollected = additionalCashCollected; }

    public String getAgentNotes() { return agentNotes; }
    public void setAgentNotes(String agentNotes) { this.agentNotes = agentNotes; }

    public String getAgentPhotosJson() { return agentPhotosJson; }
    public void setAgentPhotosJson(String agentPhotosJson) { this.agentPhotosJson = agentPhotosJson; }

    public LocalDateTime getInspectedAt() { return inspectedAt; }
    public void setInspectedAt(LocalDateTime inspectedAt) { this.inspectedAt = inspectedAt; }
}
