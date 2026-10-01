package org.axolotlik.axolotlikcosmocats.featuretoggle.exception;

public class FeatureNotAvailableException extends RuntimeException {
  public static final String FEATURE_NOT_AVAILABLE = "Feature %s is not available right now.";

  public FeatureNotAvailableException(String featureName) {
    super(String.format(FEATURE_NOT_AVAILABLE, featureName));
  }
}
