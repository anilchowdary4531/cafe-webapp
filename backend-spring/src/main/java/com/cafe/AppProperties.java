package com.cafe;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private String corsOrigin;
  private final Otp otp = new Otp();
  private final Jwt jwt = new Jwt();
  private final Auth auth = new Auth();
  private final Firebase firebase = new Firebase();

  public String getCorsOrigin() { return corsOrigin; }
  public void setCorsOrigin(String corsOrigin) { this.corsOrigin = corsOrigin; }
  public Otp getOtp() { return otp; }
  public Jwt getJwt() { return jwt; }
  public Auth getAuth() { return auth; }
  public Firebase getFirebase() { return firebase; }

  public static class Otp {
    private int expirySeconds;
    private int resendCooldownSeconds;
    private int maxAttempts;
    private String provider;
    private final Msg91 msg91 = new Msg91();

    public int getExpirySeconds() { return expirySeconds; }
    public void setExpirySeconds(int expirySeconds) { this.expirySeconds = expirySeconds; }
    public int getResendCooldownSeconds() { return resendCooldownSeconds; }
    public void setResendCooldownSeconds(int resendCooldownSeconds) { this.resendCooldownSeconds = resendCooldownSeconds; }
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Msg91 getMsg91() { return msg91; }
  }

  public static class Msg91 {
    private String authKey;
    private String senderId;
    private String route;
    private String templateId;
    private String countryCode;

    public String getAuthKey() { return authKey; }
    public void setAuthKey(String authKey) { this.authKey = authKey; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
  }

  public static class Jwt {
    private String issuer;
    private String secret;
    private long accessTtlMinutes;
    private long refreshTtlDays;

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getAccessTtlMinutes() { return accessTtlMinutes; }
    public void setAccessTtlMinutes(long accessTtlMinutes) { this.accessTtlMinutes = accessTtlMinutes; }
    public long getRefreshTtlDays() { return refreshTtlDays; }
    public void setRefreshTtlDays(long refreshTtlDays) { this.refreshTtlDays = refreshTtlDays; }
  }

  public static class Auth {
    private String adminEmail;
    private String staffEmail;

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getStaffEmail() { return staffEmail; }
    public void setStaffEmail(String staffEmail) { this.staffEmail = staffEmail; }
  }

  public static class Firebase {
    private String serviceAccountPath;

    public String getServiceAccountPath() { return serviceAccountPath; }
    public void setServiceAccountPath(String serviceAccountPath) { this.serviceAccountPath = serviceAccountPath; }
  }
}

