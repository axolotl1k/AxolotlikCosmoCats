package org.axolotlik.axolotlikcosmocats.featuretoggle.service;

import org.axolotlik.axolotlikcosmocats.config.FeatureToggleProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeatureToggleService {
  // ConcurrentHashMap гарантує thread-safety при одночасному читанні/записі.
  // Це критично, бо сервіс працює в багатопотоковому середовищі,
  // і зміна стану фічі в рантаймі не повинна ламати запити інших користувачів.
  private final ConcurrentHashMap<String, Boolean> featureToggles;

  public FeatureToggleService(FeatureToggleProperties featureToggleProperties) {
    this.featureToggles = new ConcurrentHashMap<>(featureToggleProperties.getFeature());
  }

  public boolean check(String featureName) {
    return featureToggles.getOrDefault(featureName, false);
  }

  public void enable(String featureName) {
    featureToggles.put(featureName, true);
  }

  public void disable(String featureName) {
    featureToggles.put(featureName, false);
  }
}
