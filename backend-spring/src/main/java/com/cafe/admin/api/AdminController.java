package com.cafe.admin.api;

import com.cafe.AppProperties;
import com.cafe.auth.model.AppUser;
import com.cafe.auth.model.Role;
import com.cafe.auth.repo.AppUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppProperties appProperties;

  public AdminController(
      AppUserRepository appUserRepository,
      PasswordEncoder passwordEncoder,
      AppProperties appProperties) {
    this.appUserRepository = appUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.appProperties = appProperties;
  }

  /**
   * Admin Dashboard - Returns summary of admin operations available
   */
  @GetMapping("/dashboard")
  public ResponseEntity<?> getDashboard() {
    Map<String, Object> dashboard = new HashMap<>();
    dashboard.put("menu", "Admin can create, update, delete menu items at /api/admin/menu");
    dashboard.put("tax_slabs", "Admin can manage tax slabs at /api/admin/tax-slabs");
    dashboard.put("orders", "Admin can view and manage orders at /api/orders");
    dashboard.put("service_requests", "Admin can manage service requests at /api/requests");

    return ResponseEntity.ok(dashboard);
  }

  @GetMapping("/users")
  public ResponseEntity<List<UserSummary>> listUsers() {
    List<UserSummary> users = appUserRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(user -> new UserSummary(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt()))
        .toList();
    return ResponseEntity.ok(users);
  }

  @PostMapping("/users/staff")
  @Transactional
  public ResponseEntity<?> createStaff(@RequestBody CreateStaffRequest request) {
    String email = request.email() == null ? "" : request.email().trim().toLowerCase();
    if (email.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
    }
    if (request.password() == null || request.password().length() < 6) {
      return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));
    }

    if (appUserRepository.findByEmailIgnoreCase(email).isPresent()) {
      return ResponseEntity.badRequest().body(Map.of("error", "User already exists"));
    }

    AppUser user = new AppUser();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(Role.STAFF.name());
    AppUser saved = appUserRepository.save(user);

    return ResponseEntity.ok(new UserSummary(saved.getId(), saved.getEmail(), saved.getRole(), saved.getCreatedAt()));
  }

  @PatchMapping("/users/{id}/access")
  @Transactional
  public ResponseEntity<?> updateStaffAccess(@PathVariable Long id, @RequestBody UpdateStaffAccessRequest request) {
    AppUser user = appUserRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    String protectedAdminEmail = appProperties.getAuth().getAdminEmail();
    if (protectedAdminEmail != null && user.getEmail().equalsIgnoreCase(protectedAdminEmail.trim())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Cannot modify primary admin access"));
    }

    user.setRole(Boolean.TRUE.equals(request.allowStaffAccess()) ? Role.STAFF.name() : Role.CUSTOMER.name());
    AppUser saved = appUserRepository.save(user);
    return ResponseEntity.ok(new UserSummary(saved.getId(), saved.getEmail(), saved.getRole(), saved.getCreatedAt()));
  }

  public record CreateStaffRequest(@Email String email, @NotBlank String password) {}

  public record UpdateStaffAccessRequest(Boolean allowStaffAccess) {}

  public record UserSummary(Long id, String email, String role, LocalDateTime createdAt) {}
}

