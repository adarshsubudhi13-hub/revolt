package com.revolt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_ledger")
public class PointsLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long orderId;

    private Long tradeInDeviceId;

    @Column(nullable = false)
    private Long points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointsTransactionType transactionType;

    @Column(nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PointsLedger() {}

    public PointsLedger(Long userId, Long orderId, Long tradeInDeviceId, Long points, 
                        PointsTransactionType transactionType, Long balanceAfter, String description) {
        this.userId = userId;
        this.orderId = orderId;
        this.tradeInDeviceId = tradeInDeviceId;
        this.points = points;
        this.transactionType = transactionType;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getTradeInDeviceId() { return tradeInDeviceId; }
    public void setTradeInDeviceId(Long tradeInDeviceId) { this.tradeInDeviceId = tradeInDeviceId; }

    public Long getPoints() { return points; }
    public void setPoints(Long points) { this.points = points; }

    public PointsTransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(PointsTransactionType transactionType) { this.transactionType = transactionType; }

    public Long getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(Long balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
