package org.axolotlik.axolotlikcosmocats.featuretoggle.annotation;


import org.axolotlik.axolotlikcosmocats.featuretoggle.FeatureToggles;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureToggle {
  FeatureToggles value();
}
