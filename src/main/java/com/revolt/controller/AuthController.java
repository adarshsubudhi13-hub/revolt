package com.revolt.controller;

import com.revolt.dto.AuthDtos.AuthResponse;
import com.revolt.dto.AuthDtos.LoginRequest;
import com.revolt.dto.AuthDtos.RegisterRequest;
import com.revolt.dto.AuthDtos.VerifyOtpRequest;
import com.revolt.model.OtpType;
import com.revolt.service.AuthService;
import com.revolt.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            String message = authService.register(req);
            return ResponseEntity.ok(Map.of("success", true, "message", message));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req) {
        try {
            AuthResponse resp = authService.verifyOtpAndLogin(req);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");
        String typeStr = body.getOrDefault("type", "EMAIL_VERIFICATION");
        try {
            OtpType type = OtpType.valueOf(typeStr);
            otpService.generateAndSendOtp(identifier, type);
            return ResponseEntity.ok(Map.of("success", true, "message", "A new OTP has been dispatched to " + identifier));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
        }
    }
}
