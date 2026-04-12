package com.cafe.auth.repo;

import com.cafe.auth.model.CustomerSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {
  Optional<CustomerSession> findByTableLabel(String tableLabel);
}

