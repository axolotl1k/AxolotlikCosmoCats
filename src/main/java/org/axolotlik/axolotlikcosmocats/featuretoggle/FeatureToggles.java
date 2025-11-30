package org.axolotlik.axolotlikcosmocats.featuretoggle;

import lombok.Getter;

@Getter
public enum FeatureToggles {
  //змінив імена фіч, однак використання таке ж саме
  GALACTIC_CITIZEN_REGISTRY("galactic-citizen-registry"),
  DEEP_SPACE_MARKETPLACE("deep-space-marketplace");

  private final String featureName;

  FeatureToggles(String featureName) {
    this.featureName = featureName;
  }
}
