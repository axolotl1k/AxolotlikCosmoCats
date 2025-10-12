package org.axolotlik.axolotlikcosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Order {
  private Long id;
  private List<Product> products;
  private Double totalPrice;
  private OrderStatus status;
}
