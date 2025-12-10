package org.axolotlik.axolotlikcosmocats.web;

import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Authentication & Authorization Integration Tests")
@TestPropertySource(
    properties = {
      "application.security.api-key-header=X-Api-Key",
      "application.security.keys[0].value=TEST_SUPER_SECRET_KEY",
      "application.security.keys[0].role=ROLE_USER"
    })
class AuthenticationIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should return 401 Unauthorized when requesting without any credentials")
  void shouldReturn401WhenAnonymous() throws Exception {
    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.title").value("Unauthorized"))
        .andExpect(jsonPath("$.type").value("security-error"));
  }

  @Test
  @DisplayName("Should authenticate successfully using correct API Key")
  void shouldAuthWithApiKey() throws Exception {
    mockMvc
        .perform(get("/api/v1/products").header("X-Api-Key", "TEST_SUPER_SECRET_KEY"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should authenticate successfully using Mock JWT with Roles")
  void shouldAuthWithMockJwt() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/products")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return 403 Forbidden when JWT has wrong role")
  void shouldReturn403WithWrongRoleJwt() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/orders").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
        .andExpect(status().isForbidden());
  }
}
