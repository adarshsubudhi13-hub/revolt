package com.revolt.service;

import com.revolt.dto.TradeInDtos.ValuationRequest;
import com.revolt.dto.TradeInDtos.ValuationResponse;
import com.revolt.model.ConditionGrade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ValuationEngineService {

    @Value("${revolt.valuation.points-per-rupee:1}")
    private int pointsPerRupee;

    @Value("${revolt.valuation.price-lock-days:7}")
    private int priceLockDays;

    // Baseline catalog pricing matrix
    private static final Map<String, Double> BASE_PRICES = new HashMap<>();

    static {
        // Smartphones - Apple
        BASE_PRICES.put("APPLE_IPHONE 16 PRO MAX", 144900.0);
        BASE_PRICES.put("APPLE_IPHONE 16 PRO", 119900.0);
        BASE_PRICES.put("APPLE_IPHONE 16", 79900.0);
        BASE_PRICES.put("APPLE_IPHONE 15 PRO MAX", 134900.0);
        BASE_PRICES.put("APPLE_IPHONE 15 PRO", 110000.0);
        BASE_PRICES.put("APPLE_IPHONE 15 PLUS", 75000.0);
        BASE_PRICES.put("APPLE_IPHONE 15", 69900.0);
        BASE_PRICES.put("APPLE_IPHONE 14 PRO MAX", 95000.0);
        BASE_PRICES.put("APPLE_IPHONE 14 PRO", 85000.0);
        BASE_PRICES.put("APPLE_IPHONE 14", 58000.0);
        BASE_PRICES.put("APPLE_IPHONE 13 PRO", 65000.0);
        BASE_PRICES.put("APPLE_IPHONE 13", 52000.0);
        BASE_PRICES.put("APPLE_IPHONE 12", 36000.0);
        BASE_PRICES.put("APPLE_IPHONE 11", 26000.0);

        // Smartphones - Samsung
        BASE_PRICES.put("SAMSUNG_GALAXY S24 ULTRA", 129999.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S24+", 99999.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S24", 79999.0);
        BASE_PRICES.put("SAMSUNG_GALAXY Z FOLD 5", 145000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY Z FLIP 5", 85000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S23 ULTRA", 95000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S23", 55000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S23 FE", 42000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY S22 ULTRA", 65000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY A55", 38000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY A54", 28000.0);

        // Smartphones - OnePlus, Google, Xiaomi
        BASE_PRICES.put("ONEPLUS_12", 64999.0);
        BASE_PRICES.put("ONEPLUS_12R", 39999.0);
        BASE_PRICES.put("ONEPLUS_OPEN", 120000.0);
        BASE_PRICES.put("ONEPLUS_11", 45000.0);
        BASE_PRICES.put("ONEPLUS_11R", 34000.0);
        BASE_PRICES.put("GOOGLE_PIXEL 9 PRO", 109999.0);
        BASE_PRICES.put("GOOGLE_PIXEL 9", 79999.0);
        BASE_PRICES.put("GOOGLE_PIXEL 8 PRO", 85000.0);
        BASE_PRICES.put("GOOGLE_PIXEL 8", 60000.0);
        BASE_PRICES.put("GOOGLE_PIXEL 8A", 48000.0);
        BASE_PRICES.put("GOOGLE_PIXEL 7A", 34000.0);
        BASE_PRICES.put("XIAOMI_14 ULTRA", 99999.0);
        BASE_PRICES.put("XIAOMI_14", 65000.0);

        // Laptops - Apple, Dell, Lenovo, HP, ASUS
        BASE_PRICES.put("APPLE_MACBOOK PRO 16 M3 MAX", 349900.0);
        BASE_PRICES.put("APPLE_MACBOOK PRO 14 M3 PRO", 199900.0);
        BASE_PRICES.put("APPLE_MACBOOK AIR 15 M2", 134900.0);
        BASE_PRICES.put("APPLE_MACBOOK AIR 13 M2", 99900.0);
        BASE_PRICES.put("APPLE_MACBOOK AIR M1", 72000.0);
        BASE_PRICES.put("DELL_XPS 15", 185000.0);
        BASE_PRICES.put("DELL_XPS 13 PLUS", 145000.0);
        BASE_PRICES.put("DELL_INSPIRON 16", 75000.0);
        BASE_PRICES.put("LENOVO_THINKPAD X1 CARBON", 160000.0);
        BASE_PRICES.put("LENOVO_LEGION PRO 7I", 190000.0);
        BASE_PRICES.put("LENOVO_YOGA 9I", 135000.0);
        BASE_PRICES.put("HP_SPECTRE X360", 145000.0);
        BASE_PRICES.put("HP_OMEN 16", 115000.0);
        BASE_PRICES.put("ASUS_ROG ZEPHYRUS G16", 189990.0);
        BASE_PRICES.put("ASUS_ZENBOOK DUO", 159990.0);

        // Tablets - Apple, Samsung, OnePlus
        BASE_PRICES.put("APPLE_IPAD PRO 13 M4", 129900.0);
        BASE_PRICES.put("APPLE_IPAD PRO 11 M4", 99900.0);
        BASE_PRICES.put("APPLE_IPAD AIR 11 M2", 59900.0);
        BASE_PRICES.put("APPLE_IPAD 10TH GEN", 34900.0);
        BASE_PRICES.put("APPLE_IPAD MINI 6", 45000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY TAB S9 ULTRA", 108999.0);
        BASE_PRICES.put("SAMSUNG_GALAXY TAB S9+", 85000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY TAB S9", 65000.0);
        BASE_PRICES.put("ONEPLUS_PAD 2", 39999.0);

        // Audio & Headphones
        BASE_PRICES.put("SONY_WH-1000XM5", 29990.0);
        BASE_PRICES.put("SONY_WH-1000XM4", 21990.0);
        BASE_PRICES.put("BOSE_QUIETCOMFORT ULTRA", 35900.0);
        BASE_PRICES.put("APPLE_AIRPODS MAX", 59900.0);
        BASE_PRICES.put("APPLE_AIRPODS PRO 2", 24900.0);
        BASE_PRICES.put("SENNHEISER_MOMENTUM 4", 29990.0);
        BASE_PRICES.put("MARSHALL_STANMORE III", 39999.0);

        // Wearables & Smartwatches
        BASE_PRICES.put("APPLE_WATCH ULTRA 2", 89900.0);
        BASE_PRICES.put("APPLE_WATCH ULTRA", 72000.0);
        BASE_PRICES.put("APPLE_WATCH SERIES 9", 41900.0);
        BASE_PRICES.put("APPLE_WATCH SERIES 8", 32000.0);
        BASE_PRICES.put("SAMSUNG_GALAXY WATCH 6 CLASSIC", 36999.0);
        BASE_PRICES.put("SAMSUNG_GALAXY WATCH 6", 26999.0);
        BASE_PRICES.put("GARMIN_FENIX 7 PRO", 89990.0);

        // Gaming Consoles
        BASE_PRICES.put("SONY_PLAYSTATION 5 SLIM", 54990.0);
        BASE_PRICES.put("SONY_PLAYSTATION 5", 49990.0);
        BASE_PRICES.put("MICROSOFT_XBOX SERIES X", 54990.0);
        BASE_PRICES.put("NINTENDO_SWITCH OLED", 32990.0);
        BASE_PRICES.put("VALVE_STEAM DECK OLED", 58000.0);
        BASE_PRICES.put("ASUS_ROG ALLY X", 89990.0);

        // Cameras & Drones
        BASE_PRICES.put("SONY_A7 IV", 215000.0);
        BASE_PRICES.put("CANON_EOS R6 MARK II", 225000.0);
        BASE_PRICES.put("DJI_MINI 4 PRO", 89900.0);
        BASE_PRICES.put("GOPRO_HERO 12 BLACK", 39990.0);
    }

    public ValuationResponse calculateValuation(ValuationRequest req) {
        String key = (req.getBrand() + "_" + req.getModel()).toUpperCase().trim();
        
        // Brand fallback default if exact model not found
        Double basePrice = BASE_PRICES.get(key);
        if (basePrice == null) {
            String brandUpper = req.getBrand() != null ? req.getBrand().toUpperCase().trim() : "";
            if (brandUpper.contains("APPLE")) basePrice = 55000.0;
            else if (brandUpper.contains("SAMSUNG")) basePrice = 45000.0;
            else if (brandUpper.contains("SONY")) basePrice = 40000.0;
            else if (brandUpper.contains("DELL") || brandUpper.contains("LENOVO") || brandUpper.contains("HP")) basePrice = 65000.0;
            else basePrice = 35000.0;
        }

        // 1. Age Depreciation Factor
        double ageFactor = 1.0;
        int age = req.getAgeMonths() != null ? req.getAgeMonths() : 12;
        if (age <= 6) {
            ageFactor = 1.0;
        } else if (age <= 12) {
            ageFactor = 0.85;
        } else if (age <= 24) {
            ageFactor = 0.70;
        } else {
            ageFactor = 0.50;
        }

        // 2. Condition Factor
        ConditionGrade grade = req.getConditionGrade() != null ? req.getConditionGrade() : ConditionGrade.GRADE_B;
        double conditionFactor = grade.getMultiplier();

        // 3. Accessory Bonuses
        double accessoryBonusPercent = 0.0;
        if (Boolean.TRUE.equals(req.getHasOriginalBox())) accessoryBonusPercent += 0.03;
        if (Boolean.TRUE.equals(req.getHasOriginalCharger())) accessoryBonusPercent += 0.04;
        if (Boolean.TRUE.equals(req.getHasValidBill())) accessoryBonusPercent += 0.03;

        double calculatedValue = basePrice * ageFactor * conditionFactor * (1.0 + accessoryBonusPercent);
        long points = Math.round(calculatedValue * pointsPerRupee);
        long accessoryBonusPoints = Math.round(basePrice * ageFactor * conditionFactor * accessoryBonusPercent * pointsPerRupee);

        ValuationResponse resp = new ValuationResponse();
        resp.setBasePrice(basePrice);
        resp.setAgeDiscountPercent((1.0 - ageFactor) * 100.0);
        resp.setConditionMultiplier(conditionFactor);
        resp.setBonusPointsAccessories(accessoryBonusPoints);
        resp.setEstimatedRupees(calculatedValue);
        resp.setEstimatedPoints(points);
        resp.setPriceLockUntil(LocalDateTime.now().plusDays(priceLockDays));

        return resp;
    }
}
