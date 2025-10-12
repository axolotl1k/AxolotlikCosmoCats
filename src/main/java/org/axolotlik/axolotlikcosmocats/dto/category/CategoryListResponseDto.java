package org.axolotlik.axolotlikcosmocats.dto.category;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CategoryListResponseDto {
  List<CategoryResponseDto> categories;
}
