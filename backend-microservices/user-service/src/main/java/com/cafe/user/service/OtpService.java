package com.cafe.user.service;

import com.cafe.user.entity.OtpChallenge;
import com.cafe.user.repository.OtpChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpChallengeRepository otpChallengeRepository;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 30;
    private static final int MAX_ATTEMPTS = 5;

    public OtpChallenge sendOtp(String table, String phone) {
        Optional<OtpChallenge> existing = otpChallengeRepository.findByTableLabel(table);
        LocalDateTime now = LocalDateTime.now();

        if (existing.isPresent() && existing.get().getCooldownUntil().isAfter(now)) {
            throw new RuntimeException("Cooldown active");
        }

        String code = generateOtp();
        OtpChallenge challenge = existing.orElse(new OtpChallenge());
        challenge.setTableLabel(table);
        challenge.setPhone(phone);
        challenge.setCode(code);
        challenge.setAttempts(0);
        challenge.setExpiresAt(now.plusMinutes(OTP_EXPIRY_MINUTES));
        challenge.setCooldownUntil(now.plusSeconds(COOLDOWN_SECONDS));

        return otpChallengeRepository.save(challenge);
    }

    public boolean verifyOtp(String table, String phone, String code) {
        Optional<OtpChallenge> challengeOpt = otpChallengeRepository.findByTableLabel(table);
        if (!challengeOpt.isPresent()) {
            return false;
        }

        OtpChallenge challenge = challengeOpt.get();
        if (!challenge.getPhone().equals(phone) || challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!challenge.getCode().equals(code)) {
            challenge.setAttempts(challenge.getAttempts() + 1);
            if (challenge.getAttempts() >= MAX_ATTEMPTS) {
                otpChallengeRepository.delete(challenge);
            } else {
                otpChallengeRepository.save(challenge);
            }
            return false;
        }

        otpChallengeRepository.delete(challenge);
        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
