package com.cafe.auth.model;

public enum Role {
  ADMIN("Admin user - can manage menu, tax slabs, and settings"),
  STAFF("Staff user - can take orders and manage service requests"),
  CUSTOMER("Customer user - can view menu and place orders");

  private final String description;

  Role(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}

