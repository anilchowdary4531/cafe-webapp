package com.cafe.auth.repo;

import com.cafe.auth.model.OtpChallenge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, Long> {
  Optional<OtpChallenge> findByTableLabel(String tableLabel);
}

