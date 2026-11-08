package com.revolt.dto;

import com.revolt.model.ConditionGrade;
import com.revolt.model.PhotoType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TradeInDtos {

    public static class ValuationRequest {
        private String category;
        private String brand;
        private String model;
        private Integer ageMonths;
        private ConditionGrade conditionGrade;
        private Boolean hasOriginalBox = false;
        private Boolean hasOriginalCharger = false;
        private Boolean hasValidBill = false;

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Integer getAgeMonths() { return ageMonths; }
        public void setAgeMonths(Integer ageMonths) { this.ageMonths = ageMonths; }
        public ConditionGrade getConditionGrade() { return conditionGrade; }
        public void setConditionGrade(ConditionGrade conditionGrade) { this.conditionGrade = conditionGrade; }
        public Boolean getHasOriginalBox() { return hasOriginalBox; }
        public void setHasOriginalBox(Boolean hasOriginalBox) { this.hasOriginalBox = hasOriginalBox; }
        public Boolean getHasOriginalCharger() { return hasOriginalCharger; }
        public void setHasOriginalCharger(Boolean hasOriginalCharger) { this.hasOriginalCharger = hasOriginalCharger; }
        public Boolean getHasValidBill() { return hasValidBill; }
        public void setHasValidBill(Boolean hasValidBill) { this.hasValidBill = hasValidBill; }
    }

    public static class ValuationResponse {
        private Long estimatedPoints;
        private Double estimatedRupees;
        private Double basePrice;
        private Double ageDiscountPercent;
        private Double conditionMultiplier;
        private Long bonusPointsAccessories;
        private LocalDateTime priceLockUntil;

        public Long getEstimatedPoints() { return estimatedPoints; }
        public void setEstimatedPoints(Long estimatedPoints) { this.estimatedPoints = estimatedPoints; }
        public Double getEstimatedRupees() { return estimatedRupees; }
        public void setEstimatedRupees(Double estimatedRupees) { this.estimatedRupees = estimatedRupees; }
        public Double getBasePrice() { return basePrice; }
        public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
        public Double getAgeDiscountPercent() { return ageDiscountPercent; }
        public void setAgeDiscountPercent(Double ageDiscountPercent) { this.ageDiscountPercent = ageDiscountPercent; }
        public Double getConditionMultiplier() { return conditionMultiplier; }
        public void setConditionMultiplier(Double conditionMultiplier) { this.conditionMultiplier = conditionMultiplier; }
        public Long getBonusPointsAccessories() { return bonusPointsAccessories; }
        public void setBonusPointsAccessories(Long bonusPointsAccessories) { this.bonusPointsAccessories = bonusPointsAccessories; }
        public LocalDateTime getPriceLockUntil() { return priceLockUntil; }
        public void setPriceLockUntil(LocalDateTime priceLockUntil) { this.priceLockUntil = priceLockUntil; }
    }

    public static class DeviceListingRequest {
        private String category;
        private String brand;
        private String model;
        private String specsJson;
        private Integer ageMonths;
        private ConditionGrade conditionGrade;
        private String imei;
        private Boolean hasOriginalBox = false;
        private Boolean hasOriginalCharger = false;
        private Boolean hasValidBill = false;
        private Map<PhotoType, String> photoUrls;

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
        public Map<PhotoType, String> getPhotoUrls() { return photoUrls; }
        public void setPhotoUrls(Map<PhotoType, String> photoUrls) { this.photoUrls = photoUrls; }
    }
}
