package org.axolotlik.axolotlikcosmocats.dto.cart;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CartListResponseDto {
    List<CartResponseDto> carts;
}
