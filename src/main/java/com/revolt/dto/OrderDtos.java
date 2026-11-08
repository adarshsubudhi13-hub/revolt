package com.revolt.dto;

import com.revolt.model.Order;
import com.revolt.model.OrderStatus;
import com.revolt.model.PaymentStatus;
import java.time.LocalDateTime;

public class OrderDtos {

    public static class CreateOrderRequest {
        private Long productId;
        private Integer quantity = 1;
        private Long tradeInDeviceId; // optional
        private Long pointsToRedeem = 0L;
        private String shippingAddressJson;
        private String paymentMethod; // RAZORPAY_TEST

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Long getTradeInDeviceId() { return tradeInDeviceId; }
        public void setTradeInDeviceId(Long tradeInDeviceId) { this.tradeInDeviceId = tradeInDeviceId; }
        public Long getPointsToRedeem() { return pointsToRedeem; }
        public void setPointsToRedeem(Long pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
        public String getShippingAddressJson() { return shippingAddressJson; }
        public void setShippingAddressJson(String shippingAddressJson) { this.shippingAddressJson = shippingAddressJson; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private Long userId;
        private Long agentId;
        private Long productId;
        private String productTitle;
        private Double productPrice;
        private Integer quantity;
        private Long tradeInDeviceId;
        private Double totalAmount;
        private Long pointsRedeemed;
        private Double cashPaid;
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;
        private String shippingAddressJson;
        private String handoverOtp;
        private LocalDateTime createdAt;
        private LocalDateTime deliveredAt;

        public OrderResponse(Order order) {
            this.id = order.getId();
            this.orderNumber = order.getOrderNumber();
            this.userId = order.getUserId();
            this.agentId = order.getAgentId();
            this.productId = order.getProductId();
            this.productTitle = order.getProductTitle();
            this.productPrice = order.getProductPrice();
            this.quantity = order.getQuantity();
            this.tradeInDeviceId = order.getTradeInDeviceId();
            this.totalAmount = order.getTotalAmount();
            this.pointsRedeemed = order.getPointsRedeemed();
            this.cashPaid = order.getCashPaid();
            this.orderStatus = order.getOrderStatus();
            this.paymentStatus = order.getPaymentStatus();
            this.shippingAddressJson = order.getShippingAddressJson();
            this.handoverOtp = order.getHandoverOtp();
            this.createdAt = order.getCreatedAt();
            this.deliveredAt = order.getDeliveredAt();
        }

        public Long getId() { return id; }
        public String getOrderNumber() { return orderNumber; }
        public Long getUserId() { return userId; }
        public Long getAgentId() { return agentId; }
        public Long getProductId() { return productId; }
        public String getProductTitle() { return productTitle; }
        public Double getProductPrice() { return productPrice; }
        public Integer getQuantity() { return quantity; }
        public Long getTradeInDeviceId() { return tradeInDeviceId; }
        public Double getTotalAmount() { return totalAmount; }
        public Long getPointsRedeemed() { return pointsRedeemed; }
        public Double getCashPaid() { return cashPaid; }
        public OrderStatus getOrderStatus() { return orderStatus; }
        public PaymentStatus getPaymentStatus() { return paymentStatus; }
        public String getShippingAddressJson() { return shippingAddressJson; }
        public String getHandoverOtp() { return handoverOtp; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getDeliveredAt() { return deliveredAt; }
    }
}
