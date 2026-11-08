package com.revolt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String emailType; // REGISTRATION_OTP, ORDER_CONFIRMED, TRADEIN_VERIFIED, MISMATCH_REPORT, etc.

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String status; // SENT, FAILED, QUEUED

    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public EmailLog() {}

    public EmailLog(String recipient, String subject, String emailType, String content, String status) {
        this.recipient = recipient;
        this.subject = subject;
        this.emailType = emailType;
        this.content = content;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getEmailType() { return emailType; }
    public void setEmailType(String emailType) { this.emailType = emailType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
