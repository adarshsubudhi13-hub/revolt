package com.revolt.controller;

import com.revolt.dto.AdminDtos.DashboardStatsResponse;
import com.revolt.model.*;
import com.revolt.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderRepository orderRepository;
    private final TradeInDeviceRepository tradeInRepository;
    private final InspectionReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailLogRepository emailLogRepository;

    public AdminController(OrderRepository orderRepository,
                           TradeInDeviceRepository tradeInRepository,
                           InspectionReportRepository reportRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           EmailLogRepository emailLogRepository) {
        this.orderRepository = orderRepository;
        this.tradeInRepository = tradeInRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.emailLogRepository = emailLogRepository;
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        List<Order> orders = orderRepository.findAll();
        List<TradeInDevice> tradeIns = tradeInRepository.findAll();
        List<InspectionReport> reports = reportRepository.findAll();

        long totalOrders = orders.size();
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        long totalTradeIns = tradeIns.size();
        long pointsRedeemed = orders.stream().mapToLong(Order::getPointsRedeemed).sum();
        long activeUsers = userRepository.count();
        long mismatches = reports.stream().filter(r -> !r.getIsApproved()).count();

        // Eco Impact calculation
        // Avg smartphone ~ 0.2 kg e-waste diverted, ~ 38 kg CO2 saved per recycled device
        double eWasteKg = totalTradeIns * 0.25;
        double co2SavedKg = totalTradeIns * 42.5;

        DashboardStatsResponse resp = new DashboardStatsResponse();
        resp.setTotalOrders(totalOrders);
        resp.setTotalRevenue(totalRevenue);
        resp.setTotalTradeIns(totalTradeIns);
        resp.setTotalPointsRedeemed(pointsRedeemed);
        resp.setActiveUsers(activeUsers);
        resp.setPendingInspections(orders.stream().filter(o -> o.getOrderStatus() != OrderStatus.DELIVERED && o.getOrderStatus() != OrderStatus.CANCELLED).count());
        resp.setMismatchCount(mismatches);
        resp.seteWastePreventedKg(eWasteKg);
        resp.setCo2SavedKg(co2SavedKg);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/inspection-reports")
    public ResponseEntity<List<InspectionReport>> getAllReports() {
        return ResponseEntity.ok(reportRepository.findAll());
    }

    @GetMapping("/email-logs")
    public ResponseEntity<List<EmailLog>> getEmailLogs() {
        return ResponseEntity.ok(emailLogRepository.findTop50ByOrderByTimestampDesc());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PostMapping("/assign-agent/{orderId}")
    public ResponseEntity<?> assignAgent(@PathVariable Long orderId, @RequestParam Long agentId) {
        return orderRepository.findById(orderId).map(order -> {
            order.setAgentId(agentId);
            orderRepository.save(order);
            return ResponseEntity.ok(Map.of("success", true, "message", "Agent assigned successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
