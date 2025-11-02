package org.axolotlik.axolotlikcosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNonEmptyValidator.class)
@Documented
public @interface AtLeastOneNonEmpty {
  String DEFAULT_MESSAGE = "At least one of addProductIds or removeProductIds must be non-empty";

  String message() default DEFAULT_MESSAGE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
