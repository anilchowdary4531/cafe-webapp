package com.cafe.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpDtos {
  public record OtpSendRequest(
      @NotBlank String table,
      @NotBlank @Pattern(regexp = "^\\+?\\d{10,15}$") String phone
  ) {}

  public record OtpSendResponse(
      String table,
      String maskedPhone,
      String demoCode,
      boolean smsSent,
      String expiresAt,
      String cooldownUntil
  ) {}

  public record OtpVerifyRequest(
      @NotBlank String table,
      @NotBlank @Pattern(regexp = "^\\+?\\d{10,15}$") String phone,
      @NotBlank @Pattern(regexp = "^\\d{6}$") String code
  ) {}

  public record SessionResponse(String table, String phone, String phoneMasked, String verifiedAt) {}

  public record FirebaseVerifyRequest(@NotBlank String table, @NotBlank String idToken) {}
}

