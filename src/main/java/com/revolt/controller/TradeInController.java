package com.revolt.controller;

import com.revolt.dto.TradeInDtos.DeviceListingRequest;
import com.revolt.dto.TradeInDtos.ValuationRequest;
import com.revolt.dto.TradeInDtos.ValuationResponse;
import com.revolt.model.*;
import com.revolt.repository.DevicePhotoRepository;
import com.revolt.repository.TradeInDeviceRepository;
import com.revolt.service.PointsWalletService;
import com.revolt.service.ValuationEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/tradein")
public class TradeInController {

    private final ValuationEngineService valuationEngine;
    private final TradeInDeviceRepository tradeInRepository;
    private final DevicePhotoRepository photoRepository;
    private final PointsWalletService walletService;

    public TradeInController(ValuationEngineService valuationEngine,
                             TradeInDeviceRepository tradeInRepository,
                             DevicePhotoRepository photoRepository,
                             PointsWalletService walletService) {
        this.valuationEngine = valuationEngine;
        this.tradeInRepository = tradeInRepository;
        this.photoRepository = photoRepository;
        this.walletService = walletService;
    }

    @PostMapping("/calculate-valuation")
    public ResponseEntity<ValuationResponse> calculateValuation(@RequestBody ValuationRequest req) {
        ValuationResponse resp = valuationEngine.calculateValuation(req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/catalog-models")
    public ResponseEntity<Map<String, List<String>>> getCatalogModels() {
        Map<String, List<String>> catalog = new LinkedHashMap<>();
        catalog.put("Smartphones", Arrays.asList(
                "Apple iPhone 16 Pro Max", "Apple iPhone 16", "Apple iPhone 15 Pro", "Apple iPhone 15", "Apple iPhone 14", "Apple iPhone 13", "Apple iPhone 12",
                "Samsung Galaxy S24 Ultra", "Samsung Galaxy S23", "Samsung Galaxy S23 FE", "Samsung Galaxy Z Fold 5", "Samsung Galaxy A54",
                "OnePlus 12", "OnePlus 11R", "OnePlus Open", "Google Pixel 9 Pro", "Google Pixel 8 Pro", "Google Pixel 8a", "Xiaomi 14 Ultra"
        ));
        catalog.put("Laptops", Arrays.asList(
                "Apple MacBook Pro 16 M3 Max", "Apple MacBook Air 15 M2", "Apple MacBook Air 13 M2",
                "Dell XPS 15", "Dell XPS 13 Plus", "Lenovo ThinkPad X1 Carbon", "ASUS ROG Zephyrus G16", "HP Spectre x360"
        ));
        catalog.put("Tablets", Arrays.asList(
                "Apple iPad Pro 13 M4", "Apple iPad Air 11 M2", "Apple iPad 10th Gen",
                "Samsung Galaxy Tab S9 Ultra", "Samsung Galaxy Tab S9", "OnePlus Pad 2"
        ));
        catalog.put("Audio", Arrays.asList(
                "Sony WH-1000XM5", "Sony WH-1000XM4", "Bose QuietComfort Ultra", "Apple AirPods Max", "Sennheiser Momentum 4", "Marshall Stanmore III"
        ));
        catalog.put("Wearables", Arrays.asList(
                "Apple Watch Ultra 2", "Apple Watch Series 9", "Samsung Galaxy Watch 6 Classic", "Garmin Fenix 7 Pro"
        ));
        catalog.put("Gaming", Arrays.asList(
                "Sony PlayStation 5 Slim", "Microsoft Xbox Series X", "Nintendo Switch OLED", "Valve Steam Deck OLED", "ASUS ROG Ally X"
        ));
        catalog.put("Cameras", Arrays.asList(
                "Sony A7 IV", "Canon EOS R6 Mark II", "DJI Mini 4 Pro", "GoPro Hero 12 Black"
        ));
        return ResponseEntity.ok(catalog);
    }

    @PostMapping("/submit-listing")
    public ResponseEntity<?> submitListing(@AuthenticationPrincipal User user,
                                           @RequestBody DeviceListingRequest req) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        // Check for duplicate active IMEI listing
        if (req.getImei() != null && !req.getImei().trim().isEmpty()) {
            boolean exists = tradeInRepository.existsByImeiAndStatusNot(req.getImei().trim(), TradeInStatus.CANCELLED);
            if (exists) {
                return ResponseEntity.badRequest().body(Map.of("message", 
                        "This IMEI/Serial Number is already registered in an active trade-in."));
            }
        }

        // Calculate valuation
        ValuationRequest vReq = new ValuationRequest();
        vReq.setCategory(req.getCategory());
        vReq.setBrand(req.getBrand());
        vReq.setModel(req.getModel());
        vReq.setAgeMonths(req.getAgeMonths());
        vReq.setConditionGrade(req.getConditionGrade());
        vReq.setHasOriginalBox(req.getHasOriginalBox());
        vReq.setHasOriginalCharger(req.getHasOriginalCharger());
        vReq.setHasValidBill(req.getHasValidBill());

        ValuationResponse vResp = valuationEngine.calculateValuation(vReq);

        TradeInDevice device = new TradeInDevice();
        device.setUserId(user.getId());
        device.setCategory(req.getCategory());
        device.setBrand(req.getBrand());
        device.setModel(req.getModel());
        device.setSpecsJson(req.getSpecsJson());
        device.setAgeMonths(req.getAgeMonths());
        device.setConditionGrade(req.getConditionGrade());
        device.setImei(req.getImei() != null ? req.getImei().trim() : "UNKNOWN-" + UUID.randomUUID().toString().substring(0, 8));
        device.setHasOriginalBox(Boolean.TRUE.equals(req.getHasOriginalBox()));
        device.setHasOriginalCharger(Boolean.TRUE.equals(req.getHasOriginalCharger()));
        device.setHasValidBill(Boolean.TRUE.equals(req.getHasValidBill()));
        device.setEstimatedPoints(vResp.getEstimatedPoints());
        device.setLockedUntil(vResp.getPriceLockUntil());
        device.setStatus(TradeInStatus.VALUATED);

        TradeInDevice saved = tradeInRepository.save(device);

        // Save Photos
        if (req.getPhotoUrls() != null) {
            for (Map.Entry<PhotoType, String> entry : req.getPhotoUrls().entrySet()) {
                DevicePhoto photo = new DevicePhoto(saved.getId(), entry.getKey(), entry.getValue());
                photoRepository.save(photo);
            }
        }

        // Credit points to user wallet
        walletService.creditInitialTradeInPoints(user, saved.getId(), saved.getEstimatedPoints());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "tradeInDevice", saved,
                "estimatedPoints", saved.getEstimatedPoints(),
                "priceLockExpiry", saved.getLockedUntil()
        ));
    }

    @GetMapping("/my-devices")
    public ResponseEntity<?> getMyDevices(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        List<TradeInDevice> list = tradeInRepository.findByUserId(user.getId());
        return ResponseEntity.ok(list);
    }
}
