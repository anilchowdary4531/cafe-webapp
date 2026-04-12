package com.cafe.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_session")
public class CustomerSession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_label", nullable = false, unique = true)
  private String tableLabel;

  @Column(nullable = false)
  private String phone;

  @Column(name = "verified_at", nullable = false)
  private LocalDateTime verifiedAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  public Long getId() { return id; }
  public String getTableLabel() { return tableLabel; }
  public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public LocalDateTime getVerifiedAt() { return verifiedAt; }
  public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

