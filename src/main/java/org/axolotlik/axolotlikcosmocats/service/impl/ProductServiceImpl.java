package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public List<Product> getProductsByCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    return productRepository.findAll().stream()
        .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(category.getId()))
        .toList();
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Override
  public Product createProduct(Product product, Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    product.setCategory(category);
    Long id = productRepository.generateId();
    product.setId(id);

    return productRepository.save(id, product);
  }

  @Override
  public Product updateProduct(Long id, Product updated, Long categoryId) {
    if (!productRepository.existsById(id)) {
      throw new ProductNotFoundException(id);
    }

    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    updated.setId(id);
    updated.setCategory(category);
    return productRepository.save(id, updated);
  }

  @Override
  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }
}
