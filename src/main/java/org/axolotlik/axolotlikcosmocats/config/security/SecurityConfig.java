package org.axolotlik.axolotlikcosmocats.config.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@Profile("!no-auth")
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      SecurityProperties securityProperties,
      AuthenticationEntryPoint authenticationEntryPoint,
      AccessDeniedHandler accessDeniedHandler)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint) // Для 401 (без ключа)
                    .accessDeniedHandler(accessDeniedHandler) // Для 403 (немає прав)
            )
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .addFilterBefore(
            new ApiKeyAuthenticationFilter(securityProperties),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}
