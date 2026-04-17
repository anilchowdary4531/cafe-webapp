package com.cafe.user.repository;

import com.cafe.user.entity.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerSessionRepository extends JpaRepository<CustomerSession, String> {
    Optional<CustomerSession> findByTableLabel(String tableLabel);
}
