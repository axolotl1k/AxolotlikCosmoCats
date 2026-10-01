package org.axolotlik.axolotlikcosmocats.featuretoggle.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.FeatureToggle;
import org.axolotlik.axolotlikcosmocats.featuretoggle.exception.FeatureNotAvailableException;
import org.axolotlik.axolotlikcosmocats.featuretoggle.service.FeatureToggleService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

  private final FeatureToggleService featureToggleService;

  @Before("@annotation(featureToggle)")
  public void checkFeatureEnabled(FeatureToggle featureToggle) {
    String featureName = featureToggle.value().getFeatureName();
    if (!featureToggleService.check(featureName)) {
      throw new FeatureNotAvailableException(featureName);
    }
  }
}
