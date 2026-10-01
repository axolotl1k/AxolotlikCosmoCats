package org.axolotlik.axolotlikcosmocats.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.axolotlik.axolotlikcosmocats.featuretoggle.FeatureToggleExtension;
import org.axolotlik.axolotlikcosmocats.featuretoggle.FeatureToggles;
import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.DisabledFeatureToggle;
import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.EnabledFeatureToggle;
import org.axolotlik.axolotlikcosmocats.featuretoggle.exception.FeatureNotAvailableException;
import org.axolotlik.axolotlikcosmocats.service.exception.CatNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@DisplayName("Integration tests for CosmoCatsController")
@ExtendWith(FeatureToggleExtension.class)
@WithMockUser(roles = "ADMIN")
class CosmoCatControllerIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;

  @Test
  @SneakyThrows
  @DisplayName("should return 200 OK when feature is enabled (GET all cats)")
  @EnabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturn200WhenFeatureEnabled() {
    mockMvc.perform(get("/api/v1/cosmo-cats")).andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 503 with proper error body when feature is disabled (GET all cats)")
  @DisabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturn503WhenFeatureDisabled() {
    mockMvc
        .perform(get("/api/v1/cosmo-cats"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value(503))
        .andExpect(jsonPath("$.title").value("Feature Unavailable"))
        .andExpect(
            jsonPath("$.detail")
                .value(
                    String.format(
                        FeatureNotAvailableException.FEATURE_NOT_AVAILABLE, "cosmo-cats")))
        .andExpect(jsonPath("$.instance").value("/api/v1/cosmo-cats"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 200 OK when getting specific cat and feature is enabled")
  @EnabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturn200WhenGettingSpecificCatFeatureEnabled() {
    mockMvc.perform(get("/api/v1/cosmo-cats/{name}", "Luna")).andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @DisplayName(
      "should return 503 with proper error body when getting specific cat and feature is disabled")
  @DisabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturn503WhenGettingSpecificCatFeatureDisabled() {
    mockMvc
        .perform(get("/api/v1/cosmo-cats/{name}", "Luna"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value(503))
        .andExpect(jsonPath("$.title").value("Feature Unavailable"))
        .andExpect(
            jsonPath("$.detail")
                .value(
                    String.format(
                        FeatureNotAvailableException.FEATURE_NOT_AVAILABLE, "cosmo-cats")))
        .andExpect(jsonPath("$.instance").value("/api/v1/cosmo-cats/Luna"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 with proper error body when cat not found and feature is enabled")
  @EnabledFeatureToggle(FeatureToggles.COSMO_CATS)
  void shouldReturn404WhenCatNotFoundAndFeatureEnabled() {
    mockMvc
        .perform(get("/api/v1/cosmo-cats/{name}", "Unknown"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(
            jsonPath("$.detail")
                .value(String.format(CatNotFoundException.NAME_NOT_FOUND, "Cat", "Unknown")))
        .andExpect(jsonPath("$.instance").value("/api/v1/cosmo-cats/Unknown"));
  }
}
