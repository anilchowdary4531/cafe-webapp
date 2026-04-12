package com.cafe.auth.service;

import com.cafe.AppProperties;
import com.cafe.auth.api.dto.AuthDtos;
import com.cafe.auth.model.AppUser;
import com.cafe.auth.model.RefreshToken;
import com.cafe.auth.repo.AppUserRepository;
import com.cafe.auth.repo.RefreshTokenRepository;
import com.cafe.auth.security.JwtService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final AppUserRepository appUserRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AppProperties appProperties;
  private final EmailRoleResolver emailRoleResolver;

  public AuthService(
      AppUserRepository appUserRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AppProperties appProperties,
      EmailRoleResolver emailRoleResolver
  ) {
    this.appUserRepository = appUserRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.appProperties = appProperties;
    this.emailRoleResolver = emailRoleResolver;
  }

  public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
    AppUser user = appUserRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid credentials");
    }

    String resolvedRole = emailRoleResolver.resolveRole(user.getEmail(), user.getRole());
    if (!resolvedRole.equals(user.getRole())) {
      user.setRole(resolvedRole);
      user = appUserRepository.save(user);
    }

    String accessToken = jwtService.generateAccessToken(user.getEmail(), Map.of("role", resolvedRole, "uid", user.getId()));
    String refresh = UUID.randomUUID().toString() + UUID.randomUUID();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(refresh);
    refreshToken.setExpiresAt(LocalDateTime.now().plusDays(appProperties.getJwt().getRefreshTtlDays()));
    refreshToken.setRevoked(false);
    refreshTokenRepository.save(refreshToken);

    return new AuthDtos.AuthResponse(
        accessToken,
        refresh,
        "Bearer",
        jwtService.accessTokenTtlSeconds(),
        new AuthDtos.UserInfo(user.getId(), user.getEmail(), resolvedRole)
    );
  }

  public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest request) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
        .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Refresh token expired/revoked");
    }

    AppUser user = refreshToken.getUser();
    String resolvedRole = emailRoleResolver.resolveRole(user.getEmail(), user.getRole());
    String accessToken = jwtService.generateAccessToken(user.getEmail(), Map.of("role", resolvedRole, "uid", user.getId()));

    return new AuthDtos.AuthResponse(
        accessToken,
        refreshToken.getToken(),
        "Bearer",
        jwtService.accessTokenTtlSeconds(),
        new AuthDtos.UserInfo(user.getId(), user.getEmail(), resolvedRole)
    );
  }

  public void logout(AuthDtos.LogoutRequest request) {
    refreshTokenRepository.findByToken(request.refreshToken()).ifPresent(token -> {
      token.setRevoked(true);
      refreshTokenRepository.save(token);
    });
  }
}



