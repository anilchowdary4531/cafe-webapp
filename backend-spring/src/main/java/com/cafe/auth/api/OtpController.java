package com.cafe.auth.api;

import com.cafe.auth.api.dto.OtpDtos;
import com.cafe.auth.service.FirebaseAuthService;
import com.cafe.auth.service.OtpService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class OtpController {
  private final OtpService otpService;
  private final FirebaseAuthService firebaseAuthService;

  public OtpController(OtpService otpService, FirebaseAuthService firebaseAuthService) {
    this.otpService = otpService;
    this.firebaseAuthService = firebaseAuthService;
  }

  @PostMapping("/otp/send")
  public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpDtos.OtpSendRequest request) {
    try {
      return ResponseEntity.ok(otpService.sendOtp(request.table(), request.phone()));
    } catch (IllegalStateException ex) {
      return ResponseEntity.status(429).body(Map.of("error", ex.getMessage()));
    }
  }

  @PostMapping("/otp/verify")
  public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpDtos.OtpVerifyRequest request) {
    try {
      return ResponseEntity.ok(otpService.verifyOtp(request.table(), request.phone(), request.code()));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
  }

  @GetMapping("/session/{table}")
  public ResponseEntity<?> getSession(@PathVariable String table) {
    try {
      return ResponseEntity.ok(otpService.getSession(table));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
  }

  @PostMapping("/firebase/verify")
  public ResponseEntity<?> verifyFirebase(@Valid @RequestBody OtpDtos.FirebaseVerifyRequest request) {
    if (!firebaseAuthService.isAvailable()) {
      return ResponseEntity.status(503).body(Map.of("error", "Firebase Auth is not configured on this server"));
    }
    var phone = firebaseAuthService.verifyPhoneFromIdToken(request.idToken());
    if (phone.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid Firebase token or phone not present"));
    }
    return ResponseEntity.ok(otpService.createSessionFromFirebase(request.table(), phone.get()));
  }
}



