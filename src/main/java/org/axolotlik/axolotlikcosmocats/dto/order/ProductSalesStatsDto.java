package org.axolotlik.axolotlikcosmocats.dto.order;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductSalesStatsDto {
  String productName;
  Long salesCount;
}
