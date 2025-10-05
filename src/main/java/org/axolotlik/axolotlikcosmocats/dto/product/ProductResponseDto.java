package org.axolotlik.axolotlikcosmocats.dto.product;

import lombok.Value;
import lombok.Builder;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryResponseDto;

@Value
@Builder
public class ProductResponseDto {
    Long id;
    String name;
    String description;
    Double price;
    CategoryResponseDto category;
    boolean available;
}
