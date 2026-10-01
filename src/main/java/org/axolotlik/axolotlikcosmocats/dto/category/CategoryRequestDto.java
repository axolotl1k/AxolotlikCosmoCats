package org.axolotlik.axolotlikcosmocats.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryRequestDto {
  @NotBlank(message = "must not be blank")
  String name;

  String description;
}
