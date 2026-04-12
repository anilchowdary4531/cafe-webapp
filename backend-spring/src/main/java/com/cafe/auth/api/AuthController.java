package com.cafe.auth.api;

import com.cafe.auth.api.dto.AuthDtos;
import com.cafe.auth.model.AppUser;
import com.cafe.auth.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
    try {
      return ResponseEntity.ok(authService.login(request));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@Valid @RequestBody AuthDtos.RefreshRequest request) {
    try {
      return ResponseEntity.ok(authService.refresh(request));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@Valid @RequestBody AuthDtos.LogoutRequest request) {
    authService.logout(request);
    return ResponseEntity.ok(Map.of("ok", true));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
      return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }

    return ResponseEntity.ok(new AuthDtos.UserInfo(user.getId(), user.getEmail(), user.getRole()));
  }
}

