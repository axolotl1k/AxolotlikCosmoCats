package org.axolotlik.axolotlikcosmocats.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartUpdateRequestDto;

public class AtLeastOneNonEmptyValidator implements ConstraintValidator<AtLeastOneNonEmpty, CartUpdateRequestDto> {

    @Override
    public boolean isValid(CartUpdateRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true; // handled elsewhere

        boolean hasAdd = dto.getAddProductIds() != null && !dto.getAddProductIds().isEmpty();
        boolean hasRemove = dto.getRemoveProductIds() != null && !dto.getRemoveProductIds().isEmpty();

        return hasAdd || hasRemove;
    }
}
