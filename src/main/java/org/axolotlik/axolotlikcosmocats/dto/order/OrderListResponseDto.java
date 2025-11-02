package org.axolotlik.axolotlikcosmocats.dto.order;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class OrderListResponseDto {
  List<OrderResponseDto> orders;
}
