package com.cafe.auth.security;

import com.cafe.AppProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AppProperties appProperties;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AppProperties appProperties) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.appProperties = appProperties;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/",
                "/favicon.ico",
                "/error",
                "/health",
                "/actuator/health",
                "/api/menu/**",
                "/api/auth/**",
                "/api/menu"


            ).permitAll().requestMatchers("/api/orders/**").authenticated()
            .requestMatchers("/api/super-admin/**").hasRole("SUPER_ADMIN")
            .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Prevent Spring Boot from auto-registering the filter as a plain servlet filter.
   *  It is already added to the Spring Security filter chain above. */
  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
      JwtAuthenticationFilter filter) {
    FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(filter);
    reg.setEnabled(false);
    return reg;
  }
}
