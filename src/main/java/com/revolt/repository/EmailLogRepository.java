package com.revolt.repository;

import com.revolt.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByRecipientOrderByTimestampDesc(String recipient);
    List<EmailLog> findTop50ByOrderByTimestampDesc();
}
