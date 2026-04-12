package com.cafe.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_challenge")
public class OtpChallenge {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_label", nullable = false, unique = true)
  private String tableLabel;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private int attempts;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "cooldown_until", nullable = false)
  private LocalDateTime cooldownUntil;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  public Long getId() { return id; }
  public String getTableLabel() { return tableLabel; }
  public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public int getAttempts() { return attempts; }
  public void setAttempts(int attempts) { this.attempts = attempts; }
  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
  public LocalDateTime getCooldownUntil() { return cooldownUntil; }
  public void setCooldownUntil(LocalDateTime cooldownUntil) { this.cooldownUntil = cooldownUntil; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

