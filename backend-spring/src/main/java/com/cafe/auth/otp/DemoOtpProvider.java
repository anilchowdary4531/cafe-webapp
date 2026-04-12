package com.cafe.auth.otp;

import org.springframework.stereotype.Component;

@Component
public class DemoOtpProvider implements OtpProvider {
  @Override
  public boolean sendOtp(String phone, String code) {
    return false;
  }
}

