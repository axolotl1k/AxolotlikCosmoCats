package org.axolotlik.axolotlikcosmocats.dto.order;

import lombok.Builder;
import lombok.Value;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductResponseDto;

import java.util.List;

@Value
@Builder
public class OrderResponseDto {
  Long id;
  List<ProductResponseDto> products;
  Double totalPrice;
  String status;
}
