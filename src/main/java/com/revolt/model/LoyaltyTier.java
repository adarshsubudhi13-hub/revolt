package com.revolt.model;

public enum LoyaltyTier {
    SILVER(1.0),
    GOLD(1.1),
    PLATINUM(1.25);

    private final double multiplier;

    LoyaltyTier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
