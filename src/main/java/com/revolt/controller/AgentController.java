package com.revolt.controller;

import com.revolt.dto.InspectionDtos.InspectionDetailResponse;
import com.revolt.dto.InspectionDtos.InspectionSubmitRequest;
import com.revolt.model.InspectionReport;
import com.revolt.model.Order;
import com.revolt.model.OrderStatus;
import com.revolt.model.User;
import com.revolt.service.InspectionService;
import com.revolt.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final InspectionService inspectionService;
    private final OrderService orderService;

    public AgentController(InspectionService inspectionService, OrderService orderService) {
        this.inspectionService = inspectionService;
        this.orderService = orderService;
    }

    @GetMapping("/assigned-orders")
    public ResponseEntity<List<Order>> getAssignedOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(inspectionService.getAssignedOrders(user.getId()));
    }

    @GetMapping("/inspection-details/{orderId}")
    public ResponseEntity<?> getInspectionDetails(@PathVariable Long orderId) {
        try {
            InspectionDetailResponse resp = inspectionService.getInspectionDetails(orderId);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/submit-inspection")
    public ResponseEntity<?> submitInspection(@AuthenticationPrincipal User user,
                                             @RequestBody InspectionSubmitRequest req) {
        try {
            InspectionReport report = inspectionService.submitInspection(user.getId(), req);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", report.getIsApproved() ? "Trade-in verified! Delivery finalized." : "Mismatch recorded. Discrepancy report issued.",
                    "report", report
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
