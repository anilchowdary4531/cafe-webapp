package com.cafe.auth.otp;

public interface OtpProvider {
  boolean sendOtp(String phone, String code);
}

