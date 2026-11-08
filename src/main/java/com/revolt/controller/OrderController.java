package com.revolt.controller;

import com.revolt.dto.OrderDtos.CreateOrderRequest;
import com.revolt.dto.OrderDtos.OrderResponse;
import com.revolt.model.InspectionReport;
import com.revolt.model.Order;
import com.revolt.model.User;
import com.revolt.repository.InspectionReportRepository;
import com.revolt.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InspectionReportRepository reportRepository;

    public OrderController(OrderService orderService, InspectionReportRepository reportRepository) {
        this.orderService = orderService;
        this.reportRepository = reportRepository;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal User user,
                                         @RequestBody CreateOrderRequest req) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        try {
            OrderResponse resp = orderService.createOrder(user.getId(), req);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        List<Order> list = orderService.getUserOrders(user.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@AuthenticationPrincipal User user,
                                          @PathVariable Long id) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        Optional<Order> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (!order.getUserId().equals(user.getId()) && user.getRole() == com.revolt.model.Role.CUSTOMER) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("order", order);

        // Include inspection report if available (for mismatch transparency)
        Optional<InspectionReport> reportOpt = reportRepository.findByOrderId(order.getId());
        reportOpt.ifPresent(inspectionReport -> resp.put("inspectionReport", inspectionReport));

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@AuthenticationPrincipal User user,
                                         @PathVariable Long id) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        try {
            orderService.cancelOrder(id, user.getId());
            return ResponseEntity.ok(Map.of("success", true, "message", "Order successfully cancelled. Points released."));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
