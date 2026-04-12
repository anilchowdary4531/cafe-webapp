package com.cafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class CafeBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(CafeBackendApplication.class, args);
  }
}

