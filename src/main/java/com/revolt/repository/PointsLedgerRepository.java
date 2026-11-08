package com.revolt.repository;

import com.revolt.model.PointsLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsLedgerRepository extends JpaRepository<PointsLedger, Long> {
    List<PointsLedger> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PointsLedger> findByOrderId(Long orderId);
}
