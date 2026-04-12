package com.cafe.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cafe_order")
public class CafeOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "table_label", nullable = false)
  private String tableLabel;

  private String note;

  @Column(nullable = false)
  private String status = "received";

  @Column(nullable = false)
  private BigDecimal subtotal = BigDecimal.ZERO;

  @Column(nullable = false)
  private BigDecimal tax = BigDecimal.ZERO;

  @Column(nullable = false)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(name = "customer_phone")
  private String customerPhone;

  @Column(name = "customer_phone_masked")
  private String customerPhoneMasked;

  @Column(name = "customer_email")
  private String customerEmail;

  @Column(name = "payment_status", nullable = false)
  private String paymentStatus = "unpaid";

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Column(name = "phone_verified_at")
  private LocalDateTime phoneVerifiedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<OrderItem> items = new ArrayList<>();

  // Getters & setters
  public Long getId() { return id; }
  public String getTableLabel() { return tableLabel; }
  public void setTableLabel(String tableLabel) { this.tableLabel = tableLabel; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
  public BigDecimal getTax() { return tax; }
  public void setTax(BigDecimal tax) { this.tax = tax; }
  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }
  public String getCustomerPhone() { return customerPhone; }
  public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
  public String getCustomerPhoneMasked() { return customerPhoneMasked; }
  public void setCustomerPhoneMasked(String customerPhoneMasked) { this.customerPhoneMasked = customerPhoneMasked; }
  public String getCustomerEmail() { return customerEmail; }
  public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
  public String getPaymentStatus() { return paymentStatus; }
  public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
  public LocalDateTime getPaidAt() { return paidAt; }
  public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
  public LocalDateTime getPhoneVerifiedAt() { return phoneVerifiedAt; }
  public void setPhoneVerifiedAt(LocalDateTime phoneVerifiedAt) { this.phoneVerifiedAt = phoneVerifiedAt; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
}

