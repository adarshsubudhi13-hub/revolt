package com.revolt.repository;

import com.revolt.model.InspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionReportRepository extends JpaRepository<InspectionReport, Long> {
    Optional<InspectionReport> findByOrderId(Long orderId);
    Optional<InspectionReport> findByTradeInDeviceId(Long tradeInDeviceId);
}
