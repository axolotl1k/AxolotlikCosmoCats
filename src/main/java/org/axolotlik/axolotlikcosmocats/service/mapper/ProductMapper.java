package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductResponseDto;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {CategoryMapper.class}) // Використовується і для DTO, і для Entity
public interface ProductMapper {

  // === Domain <-> DTO ===
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "available", source = "available")
  @Mapping(target = "category", source = "category")
  ProductResponseDto toProductResponseDto(Product product);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", source = "name")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "available", source = "available")
  @Mapping(target = "category", ignore = true)
  Product toProduct(ProductRequestDto dto);

  List<ProductResponseDto> toProductResponseDtoList(List<Product> products);

  default ProductListResponseDto toProductListResponseDto(List<Product> products) {
    return ProductListResponseDto.builder().products(toProductResponseDtoList(products)).build();
  }

  // === Domain <-> Entity (НОВЕ) ===
  Product toDomain(ProductEntity entity);

  ProductEntity toEntity(Product domain);

  List<Product> toDomainList(List<ProductEntity> entities);
}
