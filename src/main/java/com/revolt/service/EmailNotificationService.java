package com.revolt.service;

import com.revolt.model.EmailLog;
import com.revolt.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final EmailLogRepository emailLogRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public EmailNotificationService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Async
    public void sendEmail(String to, String subject, String emailType, String htmlBody) {
        log.info("Preparing [{}] email to: {}", emailType, to);

        EmailLog logEntry = new EmailLog();
        logEntry.setRecipient(to);
        logEntry.setSubject(subject);
        logEntry.setEmailType(emailType);
        logEntry.setContent(htmlBody);
        logEntry.setTimestamp(LocalDateTime.now());

        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                helper.setFrom("no-reply@revolt-platform.com");

                mailSender.send(message);
                logEntry.setStatus("SENT");
                log.info("Email [{}] successfully sent to {}", emailType, to);
            } catch (Exception ex) {
                logEntry.setStatus("FAILED");
                logEntry.setErrorMessage(ex.getMessage());
                log.warn("SMTP send failed (offline/dev fallback): {}. Saved to email_logs.", ex.getMessage());
            }
        } else {
            // Dev simulation mode - saved in email_logs table for audit & UI display
            logEntry.setStatus("SENT_DEV_SIMULATED");
            log.info("Dev Mode: Simulated email [{}] recorded in email_logs for {}", emailType, to);
        }

        emailLogRepository.save(logEntry);
    }

    public void sendRegistrationOtp(String to, String otpCode) {
        String subject = "ReVolt Account Verification Code: " + otpCode;
        String body = "<div style='font-family:Arial,sans-serif;padding:20px;color:#1e293b;'>" +
                "<h2 style='color:#059669;'>Welcome to ReVolt!</h2>" +
                "<p>Your 6-digit verification code is:</p>" +
                "<h1 style='letter-spacing:4px;color:#2563eb;background:#f1f5f9;padding:12px;border-radius:8px;display:inline-block;'>" + otpCode + "</h1>" +
                "<p>This code is valid for 5 minutes. Do not share this code with anyone.</p>" +
                "<hr/><p style='font-size:12px;color:#64748b;'>ReVolt Electronics Circular Trade-in Platform</p></div>";
        sendEmail(to, subject, "REGISTRATION_OTP", body);
    }

    public void sendOrderConfirmation(String to, String orderNumber, Double totalAmount, Long pointsUsed, Double cashPaid) {
        String subject = "Order Confirmed: #" + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif;padding:20px;color:#1e293b;'>" +
                "<h2 style='color:#059669;'>Your Order #" + orderNumber + " is Confirmed!</h2>" +
                "<p>Thank you for shopping on ReVolt. Here is your order breakdown:</p>" +
                "<ul>" +
                "<li><b>Order Subtotal:</b> ₹" + totalAmount + "</li>" +
                "<li><b>Trade-In Points Applied (On Hold):</b> " + pointsUsed + " pts (₹" + pointsUsed + ")</li>" +
                "<li><b>Cash Settled:</b> ₹" + cashPaid + "</li>" +
                "</ul>" +
                "<p>Our delivery agent will inspect your trade-in device at doorstep upon delivery.</p>" +
                "</div>";
        sendEmail(to, subject, "ORDER_CONFIRMED", body);
    }

    public void sendOutForDelivery(String to, String orderNumber, String handoverOtp) {
        String subject = "Out for Delivery: Order #" + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif;padding:20px;color:#1e293b;'>" +
                "<h2 style='color:#2563eb;'>Your ReVolt Order #" + orderNumber + " is Out for Delivery!</h2>" +
                "<p>Your delivery agent will arrive shortly with your new product and will inspect your trade-in device.</p>" +
                "<div style='background:#fef3c7;border-left:4px solid #f59e0b;padding:12px;margin:15px 0;'>" +
                "<p style='margin:0;'><b>Secret Handover OTP:</b></p>" +
                "<h1 style='letter-spacing:6px;color:#b45309;margin:5px 0;'>" + handoverOtp + "</h1>" +
                "<p style='margin:0;font-size:13px;'>Only provide this OTP to the delivery agent after physical inspection and handover.</p>" +
                "</div></div>";
        sendEmail(to, subject, "OUT_FOR_DELIVERY", body);
    }

    public void sendTradeInVerified(String to, String orderNumber, Long finalizedPoints) {
        String subject = "Trade-in Verified! Points Finalized for Order #" + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif;padding:20px;color:#1e293b;'>" +
                "<h2 style='color:#059669;'>Trade-In Verified Successfully!</h2>" +
                "<p>Great news! Your trade-in device matched all inspection criteria for order <b>#" + orderNumber + "</b>.</p>" +
                "<p><b>" + finalizedPoints + " ReVolt Points</b> have been permanently applied to your purchase discount.</p>" +
                "<p>Thank you for keeping electronics out of landfills and advancing circular sustainability!</p></div>";
        sendEmail(to, subject, "TRADEIN_VERIFIED", body);
    }

    public void sendTradeInMismatchReport(String to, String orderNumber, String reason, Double amountDue) {
        String subject = "Trade-In Mismatch Report for Order #" + orderNumber;
        String body = "<div style='font-family:Arial,sans-serif;padding:20px;color:#1e293b;'>" +
                "<h2 style='color:#dc2626;'>Trade-In Condition Discrepancy Flagged</h2>" +
                "<p>During doorstep verification for order <b>#" + orderNumber + "</b>, the inspector flagged a condition discrepancy:</p>" +
                "<div style='background:#fee2e2;border-left:4px solid #dc2626;padding:12px;margin:15px 0;'>" +
                "<p style='margin:0;'><b>Reason:</b> " + reason + "</p>" +
                "<p style='margin:5px 0 0;'><b>Adjusted Amount Due:</b> ₹" + amountDue + "</p>" +
                "</div>" +
                "<p>You may view the side-by-side photographic comparison in your ReVolt customer portal. You have 48 hours to raise a dispute if you believe this was an error.</p></div>";
        sendEmail(to, subject, "MISMATCH_REPORT", body);
    }
}
