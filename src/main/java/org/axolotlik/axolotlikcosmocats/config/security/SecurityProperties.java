package org.axolotlik.axolotlikcosmocats.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "application.security")
public class SecurityProperties {

  private final String apiKeyHeader;
  private final List<ApiKey> keys;

  public SecurityProperties(String apiKeyHeader, List<ApiKey> keys) {
    this.apiKeyHeader = apiKeyHeader;
    this.keys = keys;
  }

  public String getApiKeyHeader() {
    return apiKeyHeader;
  }

  public List<ApiKey> getKeys() {
    return keys;
  }

  public static class ApiKey {
    private final String value;
    private final String role;

    public ApiKey(String value, String role) {
      this.value = value;
      this.role = role;
    }

    public String getValue() {
      return value;
    }

    public String getRole() {
      return role;
    }
  }
}
