package org.axolotlik.axolotlikcosmocats.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class OrderRequestDto {
    @NotEmpty List<Long> productIds;
}
