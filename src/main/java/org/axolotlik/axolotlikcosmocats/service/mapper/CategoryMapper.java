package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryResponseDto;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  // === Domain <-> DTO ===
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  CategoryResponseDto toCategoryResponseDto(Category category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  Category toCategory(CategoryRequestDto dto);

  List<CategoryResponseDto> toCategoryResponseDtoList(List<Category> categories);

  default CategoryListResponseDto toCategoryListResponseDto(List<Category> categories) {
    return CategoryListResponseDto.builder()
        .categories(toCategoryResponseDtoList(categories))
        .build();
  }

  // === Domain <-> Entity (НОВЕ) ===
  Category toDomain(CategoryEntity entity);

  CategoryEntity toEntity(Category domain);

  List<Category> toDomainList(List<CategoryEntity> entities);
}
