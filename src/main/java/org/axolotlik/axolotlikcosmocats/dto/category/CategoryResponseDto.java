package org.axolotlik.axolotlikcosmocats.dto.category;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryResponseDto {
  Long id;
  String name;
  String description;
}
