package com.revolt.service;

import com.revolt.config.JwtTokenProvider;
import com.revolt.dto.AuthDtos.AuthResponse;
import com.revolt.dto.AuthDtos.LoginRequest;
import com.revolt.dto.AuthDtos.RegisterRequest;
import com.revolt.dto.AuthDtos.VerifyOtpRequest;
import com.revolt.model.OtpType;
import com.revolt.model.Role;
import com.revolt.model.User;
import com.revolt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtTokenProvider jwtTokenProvider, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new IllegalArgumentException("An account with this phone number already exists.");
        }

        User user = new User(
                req.getFullName(),
                req.getEmail(),
                req.getPhoneNumber(),
                passwordEncoder.encode(req.getPassword()),
                Role.CUSTOMER
        );
        userRepository.save(user);

        // Dispatch verification OTPs
        otpService.generateAndSendOtp(user.getEmail(), OtpType.EMAIL_VERIFICATION);
        otpService.generateAndSendOtp(user.getPhoneNumber(), OtpType.PHONE_VERIFICATION);

        return "Registration initiated. Verification OTPs sent to " + req.getEmail() + " and " + req.getPhoneNumber();
    }

    public AuthResponse verifyOtpAndLogin(VerifyOtpRequest req) {
        OtpType type = req.getType() != null ? OtpType.valueOf(req.getType()) : OtpType.EMAIL_VERIFICATION;
        boolean verified = otpService.verifyOtp(req.getIdentifier(), req.getOtpCode(), type);

        if (!verified) {
            throw new IllegalArgumentException("Invalid or expired OTP code.");
        }

        Optional<User> userOpt = req.getIdentifier().contains("@") 
                ? userRepository.findByEmail(req.getIdentifier()) 
                : userRepository.findByPhoneNumber(req.getIdentifier());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for identifier: " + req.getIdentifier());
        }

        User user = userOpt.get();
        if (type == OtpType.EMAIL_VERIFICATION) user.setIsEmailVerified(true);
        if (type == OtpType.PHONE_VERIFICATION) user.setIsPhoneVerified(true);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest req) {
        Optional<User> userOpt = req.getIdentifier().contains("@")
                ? userRepository.findByEmail(req.getIdentifier())
                : userRepository.findByPhoneNumber(req.getIdentifier());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email/phone or password.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email/phone or password.");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, user);
    }
}
