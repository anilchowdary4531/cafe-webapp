package com.cafe.user.repository;

import com.cafe.user.entity.OtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, String> {
    Optional<OtpChallenge> findByTableLabel(String tableLabel);
}
