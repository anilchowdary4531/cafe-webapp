package com.cafe.auth.service;

import com.cafe.auth.model.Role;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class EmailRoleResolver {
  public String resolveRole(String email, String persistedRole) {
    String normalizedRole = normalize(persistedRole).toUpperCase(Locale.ROOT);
    try {
      if (!normalizedRole.isBlank()) {
        return Role.valueOf(normalizedRole).name();
      }
    } catch (IllegalArgumentException ignored) {
      // Fallback for unknown values in legacy rows.
    }

    return Role.CUSTOMER.name();
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim();
  }
}

