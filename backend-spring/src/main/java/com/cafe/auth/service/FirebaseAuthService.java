package com.cafe.auth.service;

import com.cafe.AppProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

  private static final Logger log = LoggerFactory.getLogger(FirebaseAuthService.class);

  /** Ordered list of claim keys that may carry the verified phone number. */
  private static final List<String> PHONE_CLAIM_KEYS = List.of("phone_number", "phoneNumber", "phone");

  private final AppProperties appProperties;
  private volatile FirebaseAuth firebaseAuth;

  public FirebaseAuthService(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @PostConstruct
  public void init() {
    String serviceAccountPath = appProperties.getFirebase().getServiceAccountPath();
    if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
      log.info("Firebase service-account path not configured – Firebase auth disabled.");
      return;
    }

    try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build();
      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
      firebaseAuth = FirebaseAuth.getInstance();
      log.info("Firebase Auth initialised successfully.");
    } catch (Exception ex) {
      firebaseAuth = null;
      log.error("Failed to initialise Firebase Auth – Firebase verification will be unavailable. Cause: {}", ex.getMessage(), ex);
    }
  }

  /**
   * Returns {@code true} when Firebase Auth has been successfully initialised
   * and is ready to verify tokens.
   */
  public boolean isAvailable() {
    return firebaseAuth != null;
  }

  /**
   * Verifies the given Firebase ID token and returns the phone number attached
   * to the verified identity.
   *
   * @param idToken raw Firebase ID token supplied by the client
   * @return an {@link Optional} containing the E.164 phone number, or empty if
   *         the token is invalid, Firebase is not configured, or no phone claim
   *         is present
   */
  public Optional<String> verifyPhoneFromIdToken(String idToken) {
    if (idToken == null || idToken.isBlank()) {
      log.warn("verifyPhoneFromIdToken called with a null or blank token.");
      return Optional.empty();
    }
    if (firebaseAuth == null) {
      log.warn("Firebase Auth is not available; cannot verify ID token.");
      return Optional.empty();
    }
    try {
      FirebaseToken token = firebaseAuth.verifyIdToken(idToken);
      Map<String, Object> claims = token.getClaims();
      String phone = extractPhone(claims);
      if (phone == null) {
        log.warn("Firebase token verified for uid={} but no phone claim was found.", token.getUid());
      }
      return Optional.ofNullable(phone);
    } catch (Exception ex) {
      log.warn("Firebase ID token verification failed: {}", ex.getMessage());
      return Optional.empty();
    }
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private String extractPhone(Map<String, Object> claims) {
    for (String key : PHONE_CLAIM_KEYS) {
      Object value = claims.get(key);
      if (value instanceof String phone && !phone.isBlank()) {
        return phone;
      }
    }
    return null;
  }
}

