package com.revolt.dto;

import java.util.Map;

public class AdminDtos {

    public static class DashboardStatsResponse {
        private Long totalOrders;
        private Double totalRevenue;
        private Long totalTradeIns;
        private Long totalPointsRedeemed;
        private Long activeUsers;
        private Long pendingInspections;
        private Long mismatchCount;
        private Double eWastePreventedKg;
        private Double co2SavedKg;

        public Long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Long getTotalTradeIns() { return totalTradeIns; }
        public void setTotalTradeIns(Long totalTradeIns) { this.totalTradeIns = totalTradeIns; }
        public Long getTotalPointsRedeemed() { return totalPointsRedeemed; }
        public void setTotalPointsRedeemed(Long totalPointsRedeemed) { this.totalPointsRedeemed = totalPointsRedeemed; }
        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
        public Long getPendingInspections() { return pendingInspections; }
        public void setPendingInspections(Long pendingInspections) { this.pendingInspections = pendingInspections; }
        public Long getMismatchCount() { return mismatchCount; }
        public void setMismatchCount(Long mismatchCount) { this.mismatchCount = mismatchCount; }
        public Double geteWastePreventedKg() { return eWastePreventedKg; }
        public void seteWastePreventedKg(Double eWastePreventedKg) { this.eWastePreventedKg = eWastePreventedKg; }
        public Double getCo2SavedKg() { return co2SavedKg; }
        public void setCo2SavedKg(Double co2SavedKg) { this.co2SavedKg = co2SavedKg; }
    }

    public static class UpdateSettingsRequest {
        private Integer maxRedemptionPercent;
        private Integer priceLockDays;
        private Integer pointsPerRupee;

        public Integer getMaxRedemptionPercent() { return maxRedemptionPercent; }
        public void setMaxRedemptionPercent(Integer maxRedemptionPercent) { this.maxRedemptionPercent = maxRedemptionPercent; }
        public Integer getPriceLockDays() { return priceLockDays; }
        public void setPriceLockDays(Integer priceLockDays) { this.priceLockDays = priceLockDays; }
        public Integer getPointsPerRupee() { return pointsPerRupee; }
        public void setPointsPerRupee(Integer pointsPerRupee) { this.pointsPerRupee = pointsPerRupee; }
    }
}
