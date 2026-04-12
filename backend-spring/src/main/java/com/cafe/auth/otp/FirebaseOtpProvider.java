package com.cafe.auth.otp;

import org.springframework.stereotype.Component;

@Component
public class FirebaseOtpProvider implements OtpProvider {
  @Override
  public boolean sendOtp(String phone, String code) {
    return false;
  }
}

