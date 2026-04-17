package com.cafe.config;

import com.cafe.AppProperties;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Global CORS configuration.
 *
 * <p>Allowed origins are driven by the {@code CORS_ORIGIN} environment variable (comma-separated).
 * The default value includes localhost development origins and the production frontend at
 * {@code https://sunset-cafe-site.up.railway.app}.
 *
 * <p>Spring Security picks up this {@link CorsConfigurationSource} bean automatically when
 * {@code .cors(Customizer.withDefaults())} is used in the security filter chain.
 */
@Configuration
public class CorsConfig {

  private final AppProperties appProperties;

  public CorsConfig(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // Origins are supplied via CORS_ORIGIN env var (comma-separated list).
    // Defaults include localhost dev servers and the deployed frontend domain.
    config.setAllowedOrigins(Arrays.asList(appProperties.getCorsOrigin().split(",")));

    config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));

    // Explicit header list required when allowCredentials is true —
    // a wildcard ("*") is not permitted alongside credentials by the CORS spec.
    config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

    // Allow cookies / Authorization headers to be sent cross-origin.
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
