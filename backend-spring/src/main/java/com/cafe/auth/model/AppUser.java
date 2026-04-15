package com.cafe.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String role;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id")
  private RestaurantAccount restaurant;

  @Column(name = "is_primary_admin", nullable = false)
  private boolean primaryAdmin = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delegated_by_user_id")
  private AppUser delegatedByUser;

  @Column(name = "delegated_at")
  private LocalDateTime delegatedAt;

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public RestaurantAccount getRestaurant() { return restaurant; }
  public void setRestaurant(RestaurantAccount restaurant) { this.restaurant = restaurant; }
  public boolean isPrimaryAdmin() { return primaryAdmin; }
  public void setPrimaryAdmin(boolean primaryAdmin) { this.primaryAdmin = primaryAdmin; }
  public AppUser getDelegatedByUser() { return delegatedByUser; }
  public void setDelegatedByUser(AppUser delegatedByUser) { this.delegatedByUser = delegatedByUser; }
  public LocalDateTime getDelegatedAt() { return delegatedAt; }
  public void setDelegatedAt(LocalDateTime delegatedAt) { this.delegatedAt = delegatedAt; }
}


