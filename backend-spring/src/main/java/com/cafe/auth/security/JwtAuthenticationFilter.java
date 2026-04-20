package com.cafe.auth.security;

import com.cafe.auth.repo.AppUserRepository;
import com.cafe.auth.service.EmailRoleResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final AppUserRepository appUserRepository;
  private final EmailRoleResolver emailRoleResolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {

    String path = request.getServletPath();

    // ✅ Skip public endpoints
    if (path.startsWith("/api/auth") ||
            path.startsWith("/api/menu") ||
            path.equals("/health") ||
            path.startsWith("/actuator")) {

      filterChain.doFilter(request, response);
      return;
    }

    // No token → just continue
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String token = authHeader.substring(7);
      var claims = jwtService.parseClaims(token);
      String email = claims.getSubject();

      var user = appUserRepository.findByEmail(email).orElse(null);

      if (user != null) {
        var role = emailRoleResolver.resolveRole(user.getRole());

        var authorities = java.util.List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role)
        );

        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                user, null, authorities
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
      }

    } catch (Exception ignored) {}

    filterChain.doFilter(request, response);
  }
}
