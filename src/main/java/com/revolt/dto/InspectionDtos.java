package com.revolt.dto;

import com.revolt.model.ConditionGrade;
import java.util.List;

public class InspectionDtos {

    public static class InspectionSubmitRequest {
        private Long orderId;
        private String enteredHandoverOtp;
        private Boolean imeiMatched;
        private ConditionGrade physicalGrade;
        private Boolean isApproved;
        private String mismatchReason;
        private String agentNotes;
        private List<String> agentPhotoUrls;
        private Double additionalCashCollected = 0.0;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getEnteredHandoverOtp() { return enteredHandoverOtp; }
        public void setEnteredHandoverOtp(String enteredHandoverOtp) { this.enteredHandoverOtp = enteredHandoverOtp; }
        public Boolean getImeiMatched() { return imeiMatched; }
        public void setImeiMatched(Boolean imeiMatched) { this.imeiMatched = imeiMatched; }
        public ConditionGrade getPhysicalGrade() { return physicalGrade; }
        public void setPhysicalGrade(ConditionGrade physicalGrade) { this.physicalGrade = physicalGrade; }
        public Boolean getIsApproved() { return isApproved; }
        public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
        public String getMismatchReason() { return mismatchReason; }
        public void setMismatchReason(String mismatchReason) { this.mismatchReason = mismatchReason; }
        public String getAgentNotes() { return agentNotes; }
        public void setAgentNotes(String agentNotes) { this.agentNotes = agentNotes; }
        public List<String> getAgentPhotoUrls() { return agentPhotoUrls; }
        public void setAgentPhotoUrls(List<String> agentPhotoUrls) { this.agentPhotoUrls = agentPhotoUrls; }
        public Double getAdditionalCashCollected() { return additionalCashCollected; }
        public void setAdditionalCashCollected(Double additionalCashCollected) { this.additionalCashCollected = additionalCashCollected; }
    }

    public static class InspectionDetailResponse {
        private Long orderId;
        private String orderNumber;
        private String customerName;
        private String customerPhone;
        private String shippingAddress;
        private String productName;
        private Double cashToCollect;
        // Trade in info
        private Long tradeInDeviceId;
        private String deviceBrand;
        private String deviceModel;
        private String declaredImei;
        private ConditionGrade declaredGrade;
        private Long declaredPoints;
        private List<String> declaredPhotoUrls;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Double getCashToCollect() { return cashToCollect; }
        public void setCashToCollect(Double cashToCollect) { this.cashToCollect = cashToCollect; }
        public Long getTradeInDeviceId() { return tradeInDeviceId; }
        public void setTradeInDeviceId(Long tradeInDeviceId) { this.tradeInDeviceId = tradeInDeviceId; }
        public String getDeviceBrand() { return deviceBrand; }
        public void setDeviceBrand(String deviceBrand) { this.deviceBrand = deviceBrand; }
        public String getDeviceModel() { return deviceModel; }
        public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }
        public String getDeclaredImei() { return declaredImei; }
        public void setDeclaredImei(String declaredImei) { this.declaredImei = declaredImei; }
        public ConditionGrade getDeclaredGrade() { return declaredGrade; }
        public void setDeclaredGrade(ConditionGrade declaredGrade) { this.declaredGrade = declaredGrade; }
        public Long getDeclaredPoints() { return declaredPoints; }
        public void setDeclaredPoints(Long declaredPoints) { this.declaredPoints = declaredPoints; }
        public List<String> getDeclaredPhotoUrls() { return declaredPhotoUrls; }
        public void setDeclaredPhotoUrls(List<String> declaredPhotoUrls) { this.declaredPhotoUrls = declaredPhotoUrls; }
    }
}
