package org.axolotlik.axolotlikcosmocats.dto.product;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.axolotlik.axolotlikcosmocats.dto.validation.CosmicWordCheck;

@Value
@Builder
public class ProductRequestDto {
  @NotBlank @CosmicWordCheck String name;
  String description;

  @NotNull
  @DecimalMin("0.01")
  Double price;

  @NotNull Long categoryId;
  boolean available;
}
