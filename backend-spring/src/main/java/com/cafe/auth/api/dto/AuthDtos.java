package com.cafe.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
  public record LoginRequest(@Email String email, @NotBlank String password) {}
  public record RefreshRequest(@NotBlank String refreshToken) {}
  public record LogoutRequest(@NotBlank String refreshToken) {}

  public record AuthResponse(
      String accessToken,
      String refreshToken,
      String tokenType,
      long expiresInSeconds,
      UserInfo user
  ) {}

  public record UserInfo(Long id, String email, String role) {}
}

