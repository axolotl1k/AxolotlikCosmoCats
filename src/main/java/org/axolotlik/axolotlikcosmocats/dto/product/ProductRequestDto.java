package org.axolotlik.axolotlikcosmocats.dto.product;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.axolotlik.axolotlikcosmocats.dto.validation.CosmicWordCheck;

@Value
@Builder
public class ProductRequestDto {

  @NotBlank(message = "must not be blank")
  @CosmicWordCheck
  String name;

  String description;

  @NotNull(message = "must not be null")
  @DecimalMin(value = "0.01", message = "must be greater than or equal to 0.01")
  Double price;

  @NotNull(message = "must not be null")
  Long categoryId;

  @AssertTrue(message = "must be true to indicate availability")
  boolean available;
}
