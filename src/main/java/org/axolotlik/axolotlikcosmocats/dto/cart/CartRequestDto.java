package org.axolotlik.axolotlikcosmocats.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CartRequestDto {
  @NotEmpty(message = "must not be empty")  List<Long> productIds;
}
