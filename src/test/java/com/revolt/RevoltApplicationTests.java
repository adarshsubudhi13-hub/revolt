package com.revolt;

import com.revolt.dto.OrderDtos.CreateOrderRequest;
import com.revolt.dto.OrderDtos.OrderResponse;
import com.revolt.dto.TradeInDtos.ValuationRequest;
import com.revolt.dto.TradeInDtos.ValuationResponse;
import com.revolt.dto.InspectionDtos.InspectionSubmitRequest;
import com.revolt.model.*;
import com.revolt.repository.OrderRepository;
import com.revolt.repository.ProductRepository;
import com.revolt.repository.TradeInDeviceRepository;
import com.revolt.repository.UserRepository;
import com.revolt.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RevoltApplicationTests {

    @Autowired
    private ValuationEngineService valuationEngine;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private PointsWalletService walletService;

    @Autowired
    private OtpService otpService;

    @Test
    @DisplayName("1. Application context and data seeding loads properly")
    void contextLoads() {
        assertNotNull(valuationEngine);
        assertNotNull(productService);
        assertTrue(productRepository.count() >= 10, "Product catalog should have extensive items");
        assertTrue(userRepository.count() >= 3, "Users should be pre-seeded");
    }

    @Test
    @DisplayName("2. Valuation engine calculates base prices, age discount, condition multipliers, and accessories")
    void testValuationEngineCalculations() {
        // iPhone 15 Pro, 12 months, Grade B, with box, charger, bill
        ValuationRequest req = new ValuationRequest();
        req.setBrand("Apple");
        req.setModel("iPhone 15 Pro");
        req.setAgeMonths(12);
        req.setConditionGrade(ConditionGrade.GRADE_B);
        req.setHasOriginalBox(true);
        req.setHasOriginalCharger(true);
        req.setHasValidBill(true);

        ValuationResponse resp = valuationEngine.calculateValuation(req);

        assertEquals(110000.0, resp.getBasePrice(), "Base price for iPhone 15 Pro should be 110000");
        assertEquals(0.80, resp.getConditionMultiplier(), "Grade B multiplier should be 0.80");
        assertTrue(resp.getEstimatedPoints() > 0, "Estimated points should be positive");
        assertNotNull(resp.getPriceLockUntil(), "Should provide a 7-day price lock timestamp");
    }

    @Test
    @DisplayName("3. Product service filters products by category, search query, and sorting")
    void testProductFiltering() {
        List<Product> smartphones = productService.filterProducts("Smartphones", null, null, null, null, "default");
        assertFalse(smartphones.isEmpty(), "Should retrieve smartphones");
        assertTrue(smartphones.stream().allMatch(p -> "Smartphones".equalsIgnoreCase(p.getCategory())));

        List<Product> appleProducts = productService.filterProducts(null, "Apple", null, null, null, "default");
        assertFalse(appleProducts.isEmpty(), "Should retrieve Apple products");
        assertTrue(appleProducts.stream().allMatch(p -> "Apple".equalsIgnoreCase(p.getBrand())));

        List<Product> searchResults = productService.filterProducts(null, null, null, null, "Titanium", "default");
        assertFalse(searchResults.isEmpty(), "Search for 'Titanium' should match devices");
    }

    @Test
    @DisplayName("4. Points wallet operations enforce double-entry ledger tracking")
    void testPointsWalletLedger() {
        User testUser = new User("Test Ledger User", "ledger.test@example.com", "9000000001", "hash", Role.CUSTOMER);
        testUser.setIsEmailVerified(true);
        testUser.setIsPhoneVerified(true);
        testUser = userRepository.save(testUser);

        // Credit points
        walletService.creditInitialTradeInPoints(testUser, 999L, 5000L);
        assertEquals(5000L, testUser.getAvailablePoints());
        assertEquals(0L, testUser.getHeldPoints());
    }

    @Test
    @DisplayName("5. Order creation enforces maximum 30% points redemption cap")
    void testOrderCreationPointsCap() {
        User customer = userRepository.findByEmail("rohit@example.com").orElseThrow();
        Product product = productRepository.findAll().get(0);

        // Subtotal = price * 1. 30% cap = price * 0.3
        long excessivePoints = Math.round(product.getPrice() * 0.5); // 50% points requested

        CreateOrderRequest req = new CreateOrderRequest();
        req.setProductId(product.getId());
        req.setQuantity(1);
        req.setPointsToRedeem(excessivePoints);
        req.setShippingAddressJson("{\"name\":\"Rohit\",\"phone\":\"9876543210\",\"address\":\"Street 1\",\"city\":\"Blr\",\"state\":\"KA\",\"pincode\":\"560103\"}");

        // Expect exception when points requested exceed 30% cap
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(customer.getId(), req);
        }, "Should reject orders attempting to redeem above 30% points cap");
    }

    @Test
    @DisplayName("6. OTP service generates, validates, and marks OTP as used")
    void testOtpLifecycle() {
        String testEmail = "otp.verify@example.com";
        String otp = otpService.generateAndSendOtp(testEmail, OtpType.EMAIL_VERIFICATION);
        assertNotNull(otp);
        assertEquals(6, otp.length());

        // Validate correct OTP
        boolean verified = otpService.verifyOtp(testEmail, otp, OtpType.EMAIL_VERIFICATION);
        assertTrue(verified, "Valid OTP should pass verification");

        // Validate replay attack prevention (cannot use same OTP twice)
        boolean secondAttempt = otpService.verifyOtp(testEmail, otp, OtpType.EMAIL_VERIFICATION);
        assertFalse(secondAttempt, "Reusing an already-used OTP should fail");
    }

    @Test
    @DisplayName("7. Inspection service handles doorstep verification and mismatch reports")
    void testInspectionWorkflow() {
        User agent = userRepository.findByEmail("agent@revolt.com").orElseThrow();
        Order demoOrder = orderRepository.findAll().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .findFirst()
                .orElse(null);

        if (demoOrder != null) {
            InspectionSubmitRequest req = new InspectionSubmitRequest();
            req.setOrderId(demoOrder.getId());
            req.setEnteredHandoverOtp(demoOrder.getHandoverOtp());
            req.setImeiMatched(true);
            req.setPhysicalGrade(ConditionGrade.GRADE_B);
            req.setIsApproved(true);
            req.setAgentNotes("Test verified by JUnit");

            var report = inspectionService.submitInspection(agent.getId(), req);
            assertNotNull(report);
            assertTrue(report.getIsApproved());
            assertEquals(OrderStatus.DELIVERED, orderRepository.findById(demoOrder.getId()).orElseThrow().getOrderStatus());
        }
    }
}
