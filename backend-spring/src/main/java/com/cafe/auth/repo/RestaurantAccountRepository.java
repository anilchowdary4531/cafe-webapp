package com.cafe.auth.repo;

import com.cafe.auth.model.RestaurantAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantAccountRepository extends JpaRepository<RestaurantAccount, Long> {
  boolean existsBySlugIgnoreCase(String slug);
  Optional<RestaurantAccount> findBySlugIgnoreCase(String slug);
}

