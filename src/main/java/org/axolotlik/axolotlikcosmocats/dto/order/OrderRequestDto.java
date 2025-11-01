package org.axolotlik.axolotlikcosmocats.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderRequestDto {
  @NotNull(message = "must not be null")
  Long cartId;
}
