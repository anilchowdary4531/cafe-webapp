package com.cafe.admin.api;

import com.cafe.auth.model.AppUser;
import com.cafe.auth.model.RestaurantAccount;
import com.cafe.auth.model.Role;
import com.cafe.auth.repo.AppUserRepository;
import com.cafe.auth.repo.RestaurantAccountRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminController {
  private final AppUserRepository appUserRepository;
  private final RestaurantAccountRepository restaurantAccountRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminController(
      AppUserRepository appUserRepository,
      RestaurantAccountRepository restaurantAccountRepository,
      PasswordEncoder passwordEncoder) {
    this.appUserRepository = appUserRepository;
    this.restaurantAccountRepository = restaurantAccountRepository;
    this.passwordEncoder = passwordEncoder;
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
  public ResponseEntity<List<UserSummary>> listUsers(Authentication authentication) {
    AppUser actor = getActor(authentication);
    List<AppUser> source = Role.SUPER_ADMIN.name().equals(actor.getRole())
        ? appUserRepository.findAllByOrderByCreatedAtDesc()
        : appUserRepository.findAllByRestaurantIdOrderByCreatedAtDesc(actor.getRestaurant().getId());

    List<UserSummary> users = source.stream()
        .map(this::toSummary)
        .toList();
    return ResponseEntity.ok(users);
  }

  @PostMapping("/users/staff")
  @Transactional
  public ResponseEntity<?> createStaff(@RequestBody CreateStaffRequest request, Authentication authentication) {
    AppUser actor = getActor(authentication);
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

    Long targetRestaurantId = request.restaurantId();
    if (!Role.SUPER_ADMIN.name().equals(actor.getRole())) {
      targetRestaurantId = actor.getRestaurant() == null ? null : actor.getRestaurant().getId();
    }

    if (targetRestaurantId == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "Restaurant id is required"));
    }

    RestaurantAccount targetRestaurant;
    if (Role.SUPER_ADMIN.name().equals(actor.getRole())) {
      targetRestaurant = restaurantAccountRepository.findById(targetRestaurantId)
          .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    } else {
      targetRestaurant = actor.getRestaurant();
    }

    AppUser user = new AppUser();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(Role.STAFF.name());
    user.setPrimaryAdmin(false);
    user.setRestaurant(targetRestaurant);

    AppUser saved = appUserRepository.save(user);

    return ResponseEntity.ok(toSummary(saved));
  }

  @PatchMapping("/users/{id}/access")
  @Transactional
  public ResponseEntity<?> updateStaffAccess(@PathVariable Long id, @RequestBody UpdateStaffAccessRequest request, Authentication authentication) {
    AppUser actor = getActor(authentication);
    AppUser user = appUserRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!Role.SUPER_ADMIN.name().equals(actor.getRole())) {
      if (actor.getRestaurant() == null || user.getRestaurant() == null
          || !actor.getRestaurant().getId().equals(user.getRestaurant().getId())) {
        return ResponseEntity.status(403).body(Map.of("error", "Cannot modify user from another restaurant"));
      }
    }

    if (user.isPrimaryAdmin()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Cannot modify primary admin access"));
    }

    user.setRole(Boolean.TRUE.equals(request.allowStaffAccess()) ? Role.STAFF.name() : Role.CUSTOMER.name());
    user.setPrimaryAdmin(false);
    AppUser saved = appUserRepository.save(user);
    return ResponseEntity.ok(toSummary(saved));
  }

  @PostMapping("/users/{id}/delegate-admin")
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<?> delegateAdminAccess(@PathVariable Long id, Authentication authentication) {
    AppUser currentAdmin = getActor(authentication);
    if (!currentAdmin.isPrimaryAdmin()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Only primary admin can delegate admin access"));
    }

    if (currentAdmin.getRestaurant() == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "Admin is not attached to a restaurant"));
    }

    AppUser target = appUserRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

    if (target.getId().equals(currentAdmin.getId())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Cannot delegate admin access to self"));
    }

    if (target.getRestaurant() == null
        || !target.getRestaurant().getId().equals(currentAdmin.getRestaurant().getId())) {
      return ResponseEntity.status(403).body(Map.of("error", "Target user must belong to the same restaurant"));
    }

    if (Role.SUPER_ADMIN.name().equals(target.getRole())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Cannot delegate admin role to a super admin account"));
    }

    currentAdmin.setRole(Role.STAFF.name());
    currentAdmin.setPrimaryAdmin(false);
    appUserRepository.save(currentAdmin);

    target.setRole(Role.ADMIN.name());
    target.setPrimaryAdmin(true);
    target.setDelegatedByUser(currentAdmin);
    target.setDelegatedAt(LocalDateTime.now());
    AppUser savedTarget = appUserRepository.save(target);

    return ResponseEntity.ok(Map.of(
        "previousAdmin", toSummary(currentAdmin),
        "delegatedAdmin", toSummary(savedTarget)));
  }

  private AppUser getActor(Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
      throw new IllegalArgumentException("Unauthorized");
    }
    return user;
  }

  private UserSummary toSummary(AppUser user) {
    Long restaurantId = user.getRestaurant() == null ? null : user.getRestaurant().getId();
    String restaurantName = user.getRestaurant() == null ? null : user.getRestaurant().getName();
    Long delegatedByUserId = user.getDelegatedByUser() == null ? null : user.getDelegatedByUser().getId();

    return new UserSummary(
        user.getId(),
        user.getEmail(),
        user.getRole(),
        restaurantId,
        restaurantName,
        user.isPrimaryAdmin(),
        delegatedByUserId,
        user.getDelegatedAt(),
        user.getCreatedAt());
  }

  public record CreateStaffRequest(@Email String email, @NotBlank String password, Long restaurantId) {}

  public record UpdateStaffAccessRequest(Boolean allowStaffAccess) {}

  public record UserSummary(
      Long id,
      String email,
      String role,
      Long restaurantId,
      String restaurantName,
      boolean primaryAdmin,
      Long delegatedByUserId,
      LocalDateTime delegatedAt,
      LocalDateTime createdAt) {}
}

