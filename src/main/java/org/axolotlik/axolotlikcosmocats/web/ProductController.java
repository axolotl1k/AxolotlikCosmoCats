package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.product.*;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.axolotlik.axolotlikcosmocats.service.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper mapper;

    @GetMapping
    public ProductListResponseDto getAll(@RequestParam(required = false) Long categoryId) {
        return categoryId != null
                ? mapper.toProductListResponseDto(productService.getProductsByCategory(categoryId))
                : mapper.toProductListResponseDto(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        return mapper.toProductResponseDto(productService.getProductById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(@RequestBody @Valid ProductRequestDto dto) {
        return mapper.toProductResponseDto(
                productService.createProduct(mapper.toProduct(dto), dto.getCategoryId()));
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable Long id, @RequestBody @Valid ProductRequestDto dto) {
        return mapper.toProductResponseDto(
                productService.updateProduct(id, mapper.toProduct(dto), dto.getCategoryId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
