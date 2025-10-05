package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "category", source = "category")
    ProductResponseDto toProductResponseDto(Product product);

    @Mapping(target = "id", ignore = true) // ID is generated automatically
    @Mapping(target = "category", source = "category") // category is provided by the service layer
    Product toProduct(ProductRequestDto dto, Category category);

    List<ProductResponseDto> toProductResponseDtoList(List<Product> products);

    default ProductListResponseDto toProductListResponseDto(List<Product> products) {
        return ProductListResponseDto.builder()
                .products(toProductResponseDtoList(products))
                .build();
    }
}
