package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.product.*;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.axolotlik.axolotlikcosmocats.service.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final ProductMapper productMapper;

  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ProductListResponseDto getAll(@RequestParam(required = false) Long categoryId) {
    return categoryId != null
        ? productMapper.toProductListResponseDto(productService.getProductsByCategory(categoryId))
        : productMapper.toProductListResponseDto(productService.getAllProducts());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ProductResponseDto getById(@PathVariable Long id) {
    return productMapper.toProductResponseDto(productService.getProductById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public ProductResponseDto create(@RequestBody @Valid ProductRequestDto dto) {
    return productMapper.toProductResponseDto(
        productService.createProduct(productMapper.toProduct(dto), dto.getCategoryId()));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ProductResponseDto update(
      @PathVariable Long id, @RequestBody @Valid ProductRequestDto dto) {
    return productMapper.toProductResponseDto(
        productService.updateProduct(id, productMapper.toProduct(dto), dto.getCategoryId()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    productService.deleteProduct(id);
  }
}
