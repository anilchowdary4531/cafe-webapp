package com.cafe.auth.service;

import com.cafe.AppProperties;
import com.cafe.auth.model.Role;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class EmailRoleResolver {
  private final AppProperties appProperties;

  public EmailRoleResolver(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  public String resolveRole(String email, String persistedRole) {
    String normalizedEmail = normalize(email);
    String adminEmail = normalize(appProperties.getAuth().getAdminEmail());
    String staffEmail = normalize(appProperties.getAuth().getStaffEmail());

    if (!adminEmail.isBlank() && normalizedEmail.equals(adminEmail)) {
      return Role.ADMIN.name();
    }

    String normalizedRole = normalize(persistedRole).toUpperCase(Locale.ROOT);
    try {
      if (!normalizedRole.isBlank()) {
        return Role.valueOf(normalizedRole).name();
      }
    } catch (IllegalArgumentException ignored) {
      // Fallback to configured defaults below.
    }

    if (!staffEmail.isBlank() && normalizedEmail.equals(staffEmail)) {
      return Role.STAFF.name();
    }

    return Role.CUSTOMER.name();
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }
}

