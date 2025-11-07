package org.axolotlik.axolotlikcosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CosmicWordCheckValidator.class)
@Documented
public @interface CosmicWordCheck {
  String message() default
      "Product name must contain at least one cosmic term (e.g. star, galaxy, comet)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
