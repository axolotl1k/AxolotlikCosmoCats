package org.axolotlik.axolotlikcosmocats.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class CosmicWordCheckValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final List<String> COSMIC_WORDS = List.of(
            "star", "galaxy", "comet", "planet", "moon", "sun", "orbit", "cosmo", "astro", "nova"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // other validations should catch that

        String lower = value.toLowerCase();
        return COSMIC_WORDS.stream().anyMatch(lower::contains);
    }
}
