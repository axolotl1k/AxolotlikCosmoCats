package org.axolotlik.axolotlikcosmocats.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderStatusUpdateRequestDto {
  @NotBlank String status;
}
