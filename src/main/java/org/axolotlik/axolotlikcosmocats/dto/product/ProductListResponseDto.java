package org.axolotlik.axolotlikcosmocats.dto.product;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ProductListResponseDto {
  List<ProductResponseDto> products;
}
