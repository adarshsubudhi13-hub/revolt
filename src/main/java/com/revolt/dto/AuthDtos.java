package com.revolt.dto;

import com.revolt.model.Role;
import com.revolt.model.User;

public class AuthDtos {

    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String identifier; // email or phone
        private String password;

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class VerifyOtpRequest {
        private String identifier; // email or phone
        private String otpCode;
        private String type; // EMAIL_VERIFICATION, PHONE_VERIFICATION, LOGIN, DELIVERY_HANDOVER

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class AuthResponse {
        private String token;
        private Long userId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private Role role;
        private Long availablePoints;
        private Long heldPoints;
        private Integer trustScore;
        private String loyaltyTier;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.userId = user.getId();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.role = user.getRole();
            this.availablePoints = user.getAvailablePoints();
            this.heldPoints = user.getHeldPoints();
            this.trustScore = user.getTrustScore();
            this.loyaltyTier = user.getLoyaltyTier().name();
        }

        public String getToken() { return token; }
        public Long getUserId() { return userId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public Role getRole() { return role; }
        public Long getAvailablePoints() { return availablePoints; }
        public Long getHeldPoints() { return heldPoints; }
        public Integer getTrustScore() { return trustScore; }
        public String getLoyaltyTier() { return loyaltyTier; }
    }
}
