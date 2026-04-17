package com.cafe.user.controller;

import com.cafe.user.entity.CustomerSession;
import com.cafe.user.entity.OtpChallenge;
import com.cafe.user.service.CustomerSessionService;
import com.cafe.user.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private CustomerSessionService customerSessionService;

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String table = payload.get("table");
        String phone = payload.get("phone");

        try {
            OtpChallenge challenge = otpService.sendOtp(table, phone);
            return ResponseEntity.ok(Map.of(
                "table", table,
                "maskedPhone", maskPhone(phone),
                "demoCode", challenge.getCode(),
                "expiresAt", challenge.getExpiresAt(),
                "cooldownUntil", challenge.getCooldownUntil()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(429).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String table = payload.get("table");
        String phone = payload.get("phone");
        String code = payload.get("code");

        boolean verified = otpService.verifyOtp(table, phone, code);
        if (!verified) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid OTP"));
        }

        CustomerSession session = customerSessionService.createOrUpdateSession(table, phone);
        return ResponseEntity.ok(Map.of(
            "table", session.getTableLabel(),
            "phone", session.getPhone(),
            "phoneMasked", maskPhone(session.getPhone()),
            "verifiedAt", session.getVerifiedAt()
        ));
    }

    @GetMapping("/session/{table}")
    public ResponseEntity<?> getSession(@PathVariable String table) {
        Optional<CustomerSession> session = customerSessionService.getSession(table);
        if (session.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "No verified session"));
        }

        CustomerSession s = session.get();
        return ResponseEntity.ok(Map.of(
            "table", s.getTableLabel(),
            "phone", s.getPhone(),
            "phoneMasked", maskPhone(s.getPhone()),
            "verifiedAt", s.getVerifiedAt()
        ));
    }

    private String maskPhone(String phone) {
        if (phone.length() <= 4) return phone;
        return "*".repeat(Math.max(4, phone.length() - 4)) + phone.substring(phone.length() - 4);
    }
}
