package com.revolt.model;

public enum ConditionGrade {
    GRADE_A(1.00, "Flawless - Like new, no scratches, battery health >= 85%"),
    GRADE_B(0.80, "Good - Minor scratches, fully functional, battery health >= 80%"),
    GRADE_C(0.60, "Average - Noticeable dents or scratches, screen intact, functional"),
    GRADE_D(0.35, "Defective - Screen crack or cosmetic damage, board powers on");

    private final double multiplier;
    private final String description;

    ConditionGrade(double multiplier, String description) {
        this.multiplier = multiplier;
        this.description = description;
    }

    public double getMultiplier() { return multiplier; }
    public String getDescription() { return description; }
}
