package com.cafe.auth.otp;

import com.cafe.AppProperties;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class Msg91OtpProvider implements OtpProvider {
  private static final Logger log = LoggerFactory.getLogger(Msg91OtpProvider.class);
  private final AppProperties appProperties;
  private final WebClient webClient;

  public Msg91OtpProvider(AppProperties appProperties) {
    this.appProperties = appProperties;
    this.webClient = WebClient.builder().baseUrl("https://api.msg91.com").build();
  }

  @Override
  public boolean sendOtp(String phone, String code) {
    var cfg = appProperties.getOtp().getMsg91();
    if (cfg.getAuthKey() == null || cfg.getAuthKey().isBlank()) {
      return false;
    }

    String normalized = phone.startsWith("+") ? phone.substring(1) : phone;
    Map<String, Object> body = new HashMap<>();
    body.put("template_id", cfg.getTemplateId());
    body.put("short_url", 0);
    body.put("realTimeResponse", 1);
    body.put("recipients", new Object[] {
        Map.of("mobiles", cfg.getCountryCode() + normalized.replaceFirst("^" + cfg.getCountryCode(), ""), "OTP", code)
    });

    try {
      webClient.post()
          .uri("/api/v5/flow")
          .contentType(MediaType.APPLICATION_JSON)
          .header("authkey", cfg.getAuthKey())
          .bodyValue(body)
          .retrieve()
          .toBodilessEntity()
          .block();
      return true;
    } catch (Exception ex) {
      log.error("MSG91 OTP failed", ex);
      return false;
    }
  }
}

