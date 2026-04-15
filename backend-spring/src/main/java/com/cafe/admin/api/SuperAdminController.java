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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {
  private final AppUserRepository appUserRepository;
  private final RestaurantAccountRepository restaurantAccountRepository;
  private final PasswordEncoder passwordEncoder;

  public SuperAdminController(
	  AppUserRepository appUserRepository,
	  RestaurantAccountRepository restaurantAccountRepository,
	  PasswordEncoder passwordEncoder) {
	this.appUserRepository = appUserRepository;
	this.restaurantAccountRepository = restaurantAccountRepository;
	this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/restaurants")
  public ResponseEntity<List<RestaurantSummary>> listRestaurants() {
	List<RestaurantSummary> summaries = restaurantAccountRepository.findAll().stream()
		.map(restaurant -> {
		  String primaryAdminEmail = appUserRepository
			  .findByRestaurantIdAndRoleIgnoreCaseAndPrimaryAdminTrue(restaurant.getId(), Role.ADMIN.name())
			  .map(AppUser::getEmail)
			  .orElse("");
		  return new RestaurantSummary(
			  restaurant.getId(),
			  restaurant.getName(),
			  restaurant.getSlug(),
			  restaurant.isActive(),
			  primaryAdminEmail,
			  restaurant.getCreatedAt());
		})
		.toList();
	return ResponseEntity.ok(summaries);
  }

  @PostMapping("/restaurants")
  @Transactional
  public ResponseEntity<?> createRestaurantWithAdmin(@RequestBody CreateRestaurantAdminRequest request) {
	String restaurantName = normalizeText(request.restaurantName());
	String requestedSlug = normalizeSlug(request.restaurantSlug());
	String adminEmail = normalizeEmail(request.adminEmail());

	if (restaurantName.isBlank()) {
	  return ResponseEntity.badRequest().body(Map.of("error", "Restaurant name is required"));
	}
	if (adminEmail.isBlank()) {
	  return ResponseEntity.badRequest().body(Map.of("error", "Admin email is required"));
	}
	if (request.adminPassword() == null || request.adminPassword().length() < 6) {
	  return ResponseEntity.badRequest().body(Map.of("error", "Admin password must be at least 6 characters"));
	}

	String slug = requestedSlug.isBlank() ? generateSlug(restaurantName) : requestedSlug;
	if (slug.isBlank()) {
	  slug = "restaurant";
	}

	if (restaurantAccountRepository.existsBySlugIgnoreCase(slug)) {
	  return ResponseEntity.badRequest().body(Map.of("error", "Restaurant slug already exists"));
	}

	if (appUserRepository.findByEmailIgnoreCase(adminEmail).isPresent()) {
	  return ResponseEntity.badRequest().body(Map.of("error", "Admin email already exists"));
	}

	RestaurantAccount restaurant = new RestaurantAccount();
	restaurant.setName(restaurantName);
	restaurant.setSlug(slug);
	restaurant.setActive(true);
	restaurant.setUpdatedAt(LocalDateTime.now());
	RestaurantAccount savedRestaurant = restaurantAccountRepository.save(restaurant);

	AppUser adminUser = new AppUser();
	adminUser.setEmail(adminEmail);
	adminUser.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
	adminUser.setRole(Role.ADMIN.name());
	adminUser.setRestaurant(savedRestaurant);
	adminUser.setPrimaryAdmin(true);
	AppUser savedAdmin = appUserRepository.save(adminUser);

	return ResponseEntity.ok(new RestaurantAdminProvisionResponse(
		savedRestaurant.getId(),
		savedRestaurant.getName(),
		savedRestaurant.getSlug(),
		savedAdmin.getId(),
		savedAdmin.getEmail(),
		savedAdmin.getRole(),
		savedAdmin.getCreatedAt()));
  }

  private String normalizeText(String value) {
	return value == null ? "" : value.trim();
  }

  private String normalizeEmail(String value) {
	return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeSlug(String value) {
	String normalized = generateSlug(value == null ? "" : value.trim());
	return normalized.replaceAll("-+", "-").replaceAll("(^-|-$)", "");
  }

  private String generateSlug(String value) {
	return Objects.toString(value, "")
		.toLowerCase(Locale.ROOT)
		.replaceAll("[^a-z0-9]+", "-")
		.replaceAll("(^-|-$)", "");
  }

  public record CreateRestaurantAdminRequest(
	  @NotBlank String restaurantName,
	  String restaurantSlug,
	  @Email String adminEmail,
	  @NotBlank String adminPassword) {}

  public record RestaurantSummary(
	  Long restaurantId,
	  String restaurantName,
	  String restaurantSlug,
	  boolean active,
	  String primaryAdminEmail,
	  LocalDateTime createdAt) {}

  public record RestaurantAdminProvisionResponse(
	  Long restaurantId,
	  String restaurantName,
	  String restaurantSlug,
	  Long adminUserId,
	  String adminEmail,
	  String adminRole,
	  LocalDateTime adminCreatedAt) {}
}

