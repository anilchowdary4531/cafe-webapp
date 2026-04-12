package com.cafe.auth.service;

import com.cafe.AppProperties;
import com.cafe.auth.api.dto.OtpDtos;
import com.cafe.auth.model.CustomerSession;
import com.cafe.auth.model.OtpChallenge;
import com.cafe.auth.otp.DemoOtpProvider;
import com.cafe.auth.otp.FirebaseOtpProvider;
import com.cafe.auth.otp.Msg91OtpProvider;
import com.cafe.auth.otp.OtpProvider;
import com.cafe.auth.repo.CustomerSessionRepository;
import com.cafe.auth.repo.OtpChallengeRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
  private final AppProperties appProperties;
  private final OtpChallengeRepository otpChallengeRepository;
  private final CustomerSessionRepository customerSessionRepository;
  private final OtpProvider msg91OtpProvider;
  private final OtpProvider firebaseOtpProvider;
  private final OtpProvider demoOtpProvider;

  public OtpService(
      AppProperties appProperties,
      OtpChallengeRepository otpChallengeRepository,
      CustomerSessionRepository customerSessionRepository,
      Msg91OtpProvider msg91OtpProvider,
      FirebaseOtpProvider firebaseOtpProvider,
      DemoOtpProvider demoOtpProvider
  ) {
    this.appProperties = appProperties;
    this.otpChallengeRepository = otpChallengeRepository;
    this.customerSessionRepository = customerSessionRepository;
    this.msg91OtpProvider = msg91OtpProvider;
    this.firebaseOtpProvider = firebaseOtpProvider;
    this.demoOtpProvider = demoOtpProvider;
  }

  public OtpDtos.OtpSendResponse sendOtp(String table, String phone) {
    var existing = otpChallengeRepository.findByTableLabel(table).orElse(null);
    LocalDateTime now = LocalDateTime.now();

    if (existing != null && existing.getPhone().equals(phone) && existing.getCooldownUntil().isAfter(now)) {
      throw new IllegalStateException("Cooldown active. Try again later.");
    }

    String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    OtpChallenge challenge = existing == null ? new OtpChallenge() : existing;
    challenge.setTableLabel(table);
    challenge.setPhone(phone);
    challenge.setCode(code);
    challenge.setAttempts(0);
    challenge.setExpiresAt(now.plusSeconds(appProperties.getOtp().getExpirySeconds()));
    challenge.setCooldownUntil(now.plusSeconds(appProperties.getOtp().getResendCooldownSeconds()));
    challenge.setUpdatedAt(now);
    otpChallengeRepository.save(challenge);

    boolean smsSent = resolveProvider().sendOtp(phone, code);
    String demoCode = provider().equals("demo") ? code : null;

    return new OtpDtos.OtpSendResponse(
        table,
        maskPhone(phone),
        demoCode,
        smsSent,
        challenge.getExpiresAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        challenge.getCooldownUntil().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }

  public OtpDtos.SessionResponse verifyOtp(String table, String phone, String code) {
    OtpChallenge challenge = otpChallengeRepository.findByTableLabel(table)
        .orElseThrow(() -> new IllegalArgumentException("Send OTP first"));

    if (!challenge.getPhone().equals(phone)) {
      throw new IllegalArgumentException("Phone mismatch");
    }

    if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
      otpChallengeRepository.delete(challenge);
      throw new IllegalArgumentException("OTP expired");
    }

    if (!challenge.getCode().equals(code)) {
      int attempts = challenge.getAttempts() + 1;
      if (attempts >= appProperties.getOtp().getMaxAttempts()) {
        otpChallengeRepository.delete(challenge);
        throw new IllegalArgumentException("Too many attempts");
      }
      challenge.setAttempts(attempts);
      otpChallengeRepository.save(challenge);
      throw new IllegalArgumentException("Incorrect OTP. " + (appProperties.getOtp().getMaxAttempts() - attempts) + " attempts left.");
    }

    CustomerSession session = customerSessionRepository.findByTableLabel(table).orElseGet(CustomerSession::new);
    session.setTableLabel(table);
    session.setPhone(phone);
    session.setVerifiedAt(LocalDateTime.now());
    session.setUpdatedAt(LocalDateTime.now());
    customerSessionRepository.save(session);

    otpChallengeRepository.delete(challenge);

    return new OtpDtos.SessionResponse(
        table,
        phone,
        maskPhone(phone),
        session.getVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }

  public OtpDtos.SessionResponse getSession(String table) {
    CustomerSession session = customerSessionRepository.findByTableLabel(table)
        .orElseThrow(() -> new IllegalArgumentException("No verified session for table"));

    return new OtpDtos.SessionResponse(
        table,
        session.getPhone(),
        maskPhone(session.getPhone()),
        session.getVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }

  public OtpDtos.SessionResponse createSessionFromFirebase(String table, String phone) {
    CustomerSession session = customerSessionRepository.findByTableLabel(table).orElseGet(CustomerSession::new);
    session.setTableLabel(table);
    session.setPhone(phone);
    session.setVerifiedAt(LocalDateTime.now());
    session.setUpdatedAt(LocalDateTime.now());
    customerSessionRepository.save(session);

    return new OtpDtos.SessionResponse(
        table,
        phone,
        maskPhone(phone),
        session.getVerifiedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }

  private OtpProvider resolveProvider() {
    return switch (provider()) {
      case "msg91" -> msg91OtpProvider;
      case "firebase" -> firebaseOtpProvider;
      default -> demoOtpProvider;
    };
  }

  private String provider() {
    return appProperties.getOtp().getProvider().toLowerCase(Locale.ROOT);
  }

  private String maskPhone(String phone) {
    String digits = phone.replaceAll("\\D", "");
    if (digits.length() <= 4) return digits;
    return "x".repeat(Math.max(4, digits.length() - 4)) + digits.substring(digits.length() - 4);
  }
}

