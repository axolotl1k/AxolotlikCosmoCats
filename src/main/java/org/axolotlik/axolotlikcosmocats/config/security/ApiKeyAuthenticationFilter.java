package org.axolotlik.axolotlikcosmocats.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private final SecurityProperties securityProperties;

  public ApiKeyAuthenticationFilter(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String headerName = securityProperties.getApiKeyHeader();
    String incomingKey = request.getHeader(headerName);

    if (incomingKey != null && securityProperties.getKeys() != null) {

      Optional<SecurityProperties.ApiKey> matchingKey =
          securityProperties.getKeys().stream()
              .filter(k -> k.getValue().equals(incomingKey))
              .findFirst();

      if (matchingKey.isPresent()) {
        SecurityProperties.ApiKey keyConfig = matchingKey.get();

        var authorities =
            Collections.singletonList(new SimpleGrantedAuthority(keyConfig.getRole()));
        var authentication = new UsernamePasswordAuthenticationToken("api-user", null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
