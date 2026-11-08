package com.revolt.repository;

import com.revolt.model.Order;
import com.revolt.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByAgentIdOrderByCreatedAtDesc(Long agentId);
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByTradeInDeviceId(Long tradeInDeviceId);
}
