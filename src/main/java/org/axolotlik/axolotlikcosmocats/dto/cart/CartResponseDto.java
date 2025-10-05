package org.axolotlik.axolotlikcosmocats.dto.cart;

import lombok.Builder;
import lombok.Value;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductResponseDto;

import java.util.List;

@Value
@Builder
public class CartResponseDto {
    Long id;
    List<ProductResponseDto> products;
    Double totalPrice;
}
