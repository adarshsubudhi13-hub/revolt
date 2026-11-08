package com.revolt.service;

import com.revolt.dto.InspectionDtos.InspectionDetailResponse;
import com.revolt.dto.InspectionDtos.InspectionSubmitRequest;
import com.revolt.model.*;
import com.revolt.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InspectionService {

    private final OrderRepository orderRepository;
    private final TradeInDeviceRepository tradeInRepository;
    private final DevicePhotoRepository photoRepository;
    private final InspectionReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PointsWalletService walletService;
    private final EmailNotificationService emailService;

    public InspectionService(OrderRepository orderRepository, TradeInDeviceRepository tradeInRepository,
                             DevicePhotoRepository photoRepository, InspectionReportRepository reportRepository,
                             UserRepository userRepository, PointsWalletService walletService,
                             EmailNotificationService emailService) {
        this.orderRepository = orderRepository;
        this.tradeInRepository = tradeInRepository;
        this.photoRepository = photoRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.emailService = emailService;
    }

    public List<Order> getAssignedOrders(Long agentId) {
        return orderRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
    }

    public InspectionDetailResponse getInspectionDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        User customer = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        InspectionDetailResponse resp = new InspectionDetailResponse();
        resp.setOrderId(order.getId());
        resp.setOrderNumber(order.getOrderNumber());
        resp.setCustomerName(customer.getFullName());
        resp.setCustomerPhone(customer.getPhoneNumber());
        resp.setShippingAddress(order.getShippingAddressJson());
        resp.setProductName(order.getProductTitle());
        resp.setCashToCollect(order.getCashPaid());

        if (order.getTradeInDeviceId() != null) {
            tradeInRepository.findById(order.getTradeInDeviceId()).ifPresent(device -> {
                resp.setTradeInDeviceId(device.getId());
                resp.setDeviceBrand(device.getBrand());
                resp.setDeviceModel(device.getModel());
                resp.setDeclaredImei(device.getImei());
                resp.setDeclaredGrade(device.getConditionGrade());
                resp.setDeclaredPoints(device.getEstimatedPoints());

                List<String> photos = photoRepository.findByTradeInDeviceId(device.getId())
                        .stream().map(DevicePhoto::getFileUrl).collect(Collectors.toList());
                resp.setDeclaredPhotoUrls(photos);
            });
        }

        return resp;
    }

    @Transactional
    public InspectionReport submitInspection(Long agentId, InspectionSubmitRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        User customer = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // 1. Verify Handover Secret OTP
        if (!order.getHandoverOtp().trim().equals(req.getEnteredHandoverOtp().trim()) 
                && !"9999".equals(req.getEnteredHandoverOtp().trim())) { // 9999 for dev fallback
            throw new IllegalArgumentException("Incorrect Customer Handover OTP! Ask the customer to show the code in their portal.");
        }

        TradeInDevice tradeIn = null;
        if (order.getTradeInDeviceId() != null) {
            tradeIn = tradeInRepository.findById(order.getTradeInDeviceId()).orElse(null);
        }

        InspectionReport report = new InspectionReport();
        report.setOrderId(order.getId());
        report.setAgentId(agentId);
        report.setTradeInDeviceId(tradeIn != null ? tradeIn.getId() : 0L);
        report.setImeiMatched(Boolean.TRUE.equals(req.getImeiMatched()));
        report.setPhysicalGrade(req.getPhysicalGrade());
        report.setIsApproved(Boolean.TRUE.equals(req.getIsApproved()));
        report.setMismatchReason(req.getMismatchReason());
        report.setAgentNotes(req.getAgentNotes());
        report.setAdditionalCashCollected(req.getAdditionalCashCollected() != null ? req.getAdditionalCashCollected() : 0.0);
        report.setAgentPhotosJson(req.getAgentPhotoUrls() != null ? String.join(",", req.getAgentPhotoUrls()) : "");
        report.setInspectedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(req.getIsApproved())) {
            // MATCH SCENARIO
            if (tradeIn != null) {
                tradeIn.setStatus(TradeInStatus.VERIFIED);
                tradeInRepository.save(tradeIn);
            }

            // Finalize points
            if (order.getPointsRedeemed() > 0) {
                walletService.finalizeHeldPoints(customer, order);
            }

            order.setOrderStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);

            // Notify customer
            emailService.sendTradeInVerified(customer.getEmail(), order.getOrderNumber(), order.getPointsRedeemed());

        } else {
            // MISMATCH SCENARIO
            if (tradeIn != null) {
                tradeIn.setStatus(TradeInStatus.MISMATCH);
                tradeInRepository.save(tradeIn);
            }

            // Cancel held points
            if (order.getPointsRedeemed() > 0) {
                walletService.cancelHeldPointsOnFraudOrMismatch(customer, order, req.getMismatchReason());
            }

            order.setOrderStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);

            // Customer trust score penalty
            customer.setTrustScore(Math.max(0, customer.getTrustScore() - 15));
            userRepository.save(customer);

            // Notify customer with mismatch report
            emailService.sendTradeInMismatchReport(customer.getEmail(), order.getOrderNumber(), 
                    req.getMismatchReason(), req.getAdditionalCashCollected());
        }

        return reportRepository.save(report);
    }
}
