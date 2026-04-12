package com.cafe.request.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "service_request")
public class ServiceRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_label", nullable = false)
  private String tableLabel;

  @Column(nullable = false)
  private String type;

  private String note;

  @Column(nullable = false)
  private String status = "pending";

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "JSONB")
  private String items;

  @Column(name = "customer_phone")
  private String customerPhone;

  @Column(name = "customer_phone_masked")
  private String customerPhoneMasked;

  @Column(name = "phone_verified_at")
  private LocalDateTime phoneVerifiedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  // Getters & setters
  public Long getId() { return id; }
  public String getTableLabel() { return tableLabel; }
  public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getItems() { return items; }
  public void setItems(String items) { this.items = items; }
  public String getCustomerPhone() { return customerPhone; }
  public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
  public String getCustomerPhoneMasked() { return customerPhoneMasked; }
  public void setCustomerPhoneMasked(String customerPhoneMasked) { this.customerPhoneMasked = customerPhoneMasked; }
  public LocalDateTime getPhoneVerifiedAt() { return phoneVerifiedAt; }
  public void setPhoneVerifiedAt(LocalDateTime phoneVerifiedAt) { this.phoneVerifiedAt = phoneVerifiedAt; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

