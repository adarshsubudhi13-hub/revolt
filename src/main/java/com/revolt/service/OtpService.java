package com.revolt.service;

import com.revolt.model.OtpType;
import com.revolt.model.OtpVerification;
import com.revolt.repository.OtpVerificationRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final EmailNotificationService emailService;
    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpVerificationRepository otpRepository, EmailNotificationService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public String generateAndSendOtp(String identifier, OtpType type) {
        // Generate 6 digit code
        int code = 100000 + random.nextInt(900000);
        String otpCode = String.valueOf(code);

        // Dev mode bypass for predictable testing if desired, or random
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        OtpVerification record = new OtpVerification(identifier, otpCode, type, expiresAt);
        otpRepository.save(record);

        if (identifier.contains("@")) {
            emailService.sendRegistrationOtp(identifier, otpCode);
        } else {
            // SMS simulation (logged for developer audit)
            System.out.println(">>> [SMS GATEWAY DISPATCH] To: " + identifier + " | OTP: " + otpCode + " | Type: " + type);
        }

        return otpCode;
    }

    public boolean verifyOtp(String identifier, String otpCode, OtpType type) {
        // For testing convenience: allow '123456' as master dev OTP or real generated OTP
        if ("123456".equals(otpCode)) {
            return true;
        }

        Optional<OtpVerification> opt = otpRepository.findTopByIdentifierAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
                identifier, type);

        if (opt.isEmpty()) {
            return false;
        }

        OtpVerification record = opt.get();
        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (record.getOtpCode().equals(otpCode)) {
            record.setIsUsed(true);
            otpRepository.save(record);
            return true;
        }

        return false;
    }
}
