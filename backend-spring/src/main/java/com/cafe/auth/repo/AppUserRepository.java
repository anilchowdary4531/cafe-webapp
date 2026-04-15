package com.cafe.auth.repo;

import com.cafe.auth.model.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);
  Optional<AppUser> findByEmailIgnoreCase(String email);
  List<AppUser> findAllByOrderByCreatedAtDesc();
  List<AppUser> findAllByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
  Optional<AppUser> findByRestaurantIdAndRoleIgnoreCaseAndPrimaryAdminTrue(Long restaurantId, String role);
}


