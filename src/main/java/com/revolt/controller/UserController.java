package com.revolt.controller;

import com.revolt.model.PointsLedger;
import com.revolt.model.User;
import com.revolt.repository.PointsLedgerRepository;
import com.revolt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final PointsLedgerRepository ledgerRepository;

    public UserController(UserRepository userRepository, PointsLedgerRepository ledgerRepository) {
        this.userRepository = userRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        User freshUser = userRepository.findById(user.getId()).orElse(user);
        List<PointsLedger> ledger = ledgerRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        Map<String, Object> resp = new HashMap<>();
        resp.put("user", freshUser);
        resp.put("ledger", ledger);
        return ResponseEntity.ok(resp);
    }
}
