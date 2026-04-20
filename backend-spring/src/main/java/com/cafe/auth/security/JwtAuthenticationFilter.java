package com.cafe.auth.security;

import com.cafe.auth.model.AppUser;
import com.cafe.auth.repo.AppUserRepository;
import com.cafe.auth.service.EmailRoleResolver;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final AppUserRepository appUserRepository;
  private final EmailRoleResolver emailRoleResolver;

  public JwtAuthenticationFilter(JwtService jwtService, AppUserRepository appUserRepository, EmailRoleResolver emailRoleResolver) {
    this.jwtService = jwtService;
    this.appUserRepository = appUserRepository;
    this.emailRoleResolver = emailRoleResolver;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {

    String path = request.getServletPath(); // ✅ ADD THIS

    String authHeader = request.getHeader("Authorization");

    // ✅ Skip public APIs
    if (path.startsWith("/api/auth") ||
            path.startsWith("/api/menu") ||
            path.equals("/health") ||
            path.startsWith("/actuator")) {

      filterChain.doFilter(request, response);
      return;
    }

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String token = authHeader.substring(7);
      Claims claims = jwtService.parseClaims(token);
      String email = claims.getSubject();

      AppUser user = appUserRepository.findByEmail(email).orElse(null);

      if (user != null) {
        String resolvedRole = emailRoleResolver.resolveRole(user.getRole());

        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + resolvedRole)
        );

        var auth = new UsernamePasswordAuthenticationToken(
                user, null, authorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
      }

    } catch (Exception ignored) {}

    filterChain.doFilter(request, response);
  }

}

