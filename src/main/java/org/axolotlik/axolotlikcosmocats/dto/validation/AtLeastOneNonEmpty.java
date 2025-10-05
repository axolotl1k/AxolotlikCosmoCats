package org.axolotlik.axolotlikcosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNonEmptyValidator.class)
@Documented
public @interface AtLeastOneNonEmpty {
    String message() default "At least one of addProductIds or removeProductIds must be non-empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
