package com.revolt.service;

import com.revolt.dto.OrderDtos.CreateOrderRequest;
import com.revolt.dto.OrderDtos.OrderResponse;
import com.revolt.model.*;
import com.revolt.repository.OrderRepository;
import com.revolt.repository.ProductRepository;
import com.revolt.repository.TradeInDeviceRepository;
import com.revolt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TradeInDeviceRepository tradeInRepository;
    private final UserRepository userRepository;
    private final PointsWalletService walletService;
    private final EmailNotificationService emailService;
    private final Random random = new SecureRandom();

    @Value("${revolt.valuation.max-redemption-percent:30}")
    private int maxRedemptionPercent;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        TradeInDeviceRepository tradeInRepository, UserRepository userRepository,
                        PointsWalletService walletService, EmailNotificationService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.tradeInRepository = tradeInRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.emailService = emailService;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStockQuantity() < req.getQuantity()) {
            throw new IllegalStateException("Insufficient product stock.");
        }

        double totalAmount = product.getPrice() * req.getQuantity();

        // 1. Calculate and enforce points redemption cap
        long pointsRequested = req.getPointsToRedeem() != null ? req.getPointsToRedeem() : 0L;
        long maxRedeemablePoints = Math.round(totalAmount * (maxRedemptionPercent / 100.0));

        if (pointsRequested > maxRedeemablePoints) {
            throw new IllegalArgumentException("Points redemption exceeds the maximum allowed cap of " 
                    + maxRedemptionPercent + "% (Max: " + maxRedeemablePoints + " pts)");
        }

        if (pointsRequested > user.getAvailablePoints()) {
            throw new IllegalArgumentException("Insufficient points balance. Available: " + user.getAvailablePoints());
        }

        double cashPaid = totalAmount - pointsRequested;

        // 2. Generate unique order number and 4-digit secret delivery handover OTP
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNumber = "RV-" + dateStr + "-" + (1000 + random.nextInt(9000));
        String handoverOtp = String.valueOf(1000 + random.nextInt(9000));

        // 3. Create Order
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductTitle(product.getTitle());
        order.setProductPrice(product.getPrice());
        order.setQuantity(req.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setPointsRedeemed(pointsRequested);
        order.setCashPaid(cashPaid);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setShippingAddressJson(req.getShippingAddressJson());
        order.setHandoverOtp(handoverOtp);

        // Assign default agent if available
        List<User> agents = userRepository.findByRole(Role.AGENT);
        if (!agents.isEmpty()) {
            order.setAgentId(agents.get(0).getId());
        }

        // Link Trade-in device if provided
        if (req.getTradeInDeviceId() != null) {
            Optional<TradeInDevice> tradeInOpt = tradeInRepository.findById(req.getTradeInDeviceId());
            if (tradeInOpt.isPresent()) {
                TradeInDevice tradeIn = tradeInOpt.get();
                tradeIn.setStatus(TradeInStatus.APPLIED_TO_ORDER);
                tradeInRepository.save(tradeIn);
                order.setTradeInDeviceId(tradeIn.getId());
            }
        }

        Order savedOrder = orderRepository.save(order);

        // 4. Update Product Stock
        product.setStockQuantity(product.getStockQuantity() - req.getQuantity());
        productRepository.save(product);

        // 5. Hold Points in Ledger
        if (pointsRequested > 0) {
            walletService.holdPointsForOrder(user, savedOrder, pointsRequested);
        }

        // 6. Asynchronous Email dispatch
        emailService.sendOrderConfirmation(user.getEmail(), orderNumber, totalAmount, pointsRequested, cashPaid);

        return new OrderResponse(savedOrder);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setOrderStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updated = orderRepository.save(order);

        // Send status email
        userRepository.findById(order.getUserId()).ifPresent(user -> {
            if (newStatus == OrderStatus.OUT_FOR_DELIVERY) {
                emailService.sendOutForDelivery(user.getEmail(), order.getOrderNumber(), order.getHandoverOtp());
            }
        });

        return updated;
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to cancel this order");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Restore product stock
        productRepository.findById(order.getProductId()).ifPresent(p -> {
            p.setStockQuantity(p.getStockQuantity() + order.getQuantity());
            productRepository.save(p);
        });

        // Release points
        if (order.getPointsRedeemed() > 0) {
            userRepository.findById(userId).ifPresent(user -> {
                walletService.releaseHeldPointsOnCancel(user, order);
            });
        }
    }
}
