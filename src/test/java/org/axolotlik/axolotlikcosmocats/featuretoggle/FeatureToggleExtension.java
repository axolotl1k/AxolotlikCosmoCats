package org.axolotlik.axolotlikcosmocats.featuretoggle;

import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.DisabledFeatureToggle;
import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.EnabledFeatureToggle;
import org.axolotlik.axolotlikcosmocats.featuretoggle.service.FeatureToggleService;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class FeatureToggleExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              FeatureToggleService service = getFeatureToggleService(context);

              if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                var annotation = method.getAnnotation(EnabledFeatureToggle.class);
                service.enable(annotation.value().getFeatureName());
              } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                var annotation = method.getAnnotation(DisabledFeatureToggle.class);
                service.disable(annotation.value().getFeatureName());
              }
            });
  }

  @Override
  public void afterEach(ExtensionContext context) {
    context
        .getTestMethod()
        .ifPresent(
            method -> {
              String featureName = null;
              if (method.isAnnotationPresent(EnabledFeatureToggle.class)) {
                featureName =
                    method.getAnnotation(EnabledFeatureToggle.class).value().getFeatureName();
              } else if (method.isAnnotationPresent(DisabledFeatureToggle.class)) {
                featureName =
                    method.getAnnotation(DisabledFeatureToggle.class).value().getFeatureName();
              }

              if (featureName != null) {
                FeatureToggleService service = getFeatureToggleService(context);
                boolean originalState = getFeaturePropertyFromYaml(context, featureName);
                if (originalState) {
                  service.enable(featureName);
                } else {
                  service.disable(featureName);
                }
              }
            });
  }

  private boolean getFeaturePropertyFromYaml(ExtensionContext context, String featureName) {
    Environment env = SpringExtension.getApplicationContext(context).getEnvironment();
    return env.getProperty("application.feature." + featureName, Boolean.class, Boolean.FALSE);
  }

  private FeatureToggleService getFeatureToggleService(ExtensionContext context) {
    return SpringExtension.getApplicationContext(context).getBean(FeatureToggleService.class);
  }
}
