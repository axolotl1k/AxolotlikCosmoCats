package org.axolotlik.axolotlikcosmocats.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "application")
public class FeatureToggleProperties {

  private Map<String, Boolean> feature = new HashMap<>();

}
