package org.axolotlik.axolotlikcosmocats.dto.cart;

import lombok.Builder;
import lombok.Value;
import org.axolotlik.axolotlikcosmocats.dto.validation.AtLeastOneNonEmpty;

import java.util.List;

@Value
@Builder
@AtLeastOneNonEmpty
public class CartUpdateRequestDto {
    List<Long> addProductIds;
    List<Long> removeProductIds;
}
