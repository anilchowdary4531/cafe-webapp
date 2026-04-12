package com.cafe.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_item")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private CafeOrder order;

  @Column(name = "menu_item_id")
  private Long menuItemId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private int qty = 1;

  @Column(nullable = false)
  private BigDecimal price = BigDecimal.ZERO;

  @Column(nullable = false)
  private boolean restricted = false;

  // Getters & setters
  public Long getId() { return id; }
  public CafeOrder getOrder() { return order; }
  public void setOrder(CafeOrder order) { this.order = order; }
  public Long getMenuItemId() { return menuItemId; }
  public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getQty() { return qty; }
  public void setQty(int qty) { this.qty = qty; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public boolean isRestricted() { return restricted; }
  public void setRestricted(boolean restricted) { this.restricted = restricted; }
}

