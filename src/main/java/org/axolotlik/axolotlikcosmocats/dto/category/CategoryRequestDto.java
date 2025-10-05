package org.axolotlik.axolotlikcosmocats.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryRequestDto {
    @NotBlank String name;
    String description;
}
