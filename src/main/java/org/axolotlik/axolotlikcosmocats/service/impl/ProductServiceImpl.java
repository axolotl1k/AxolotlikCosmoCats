package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @Override
  public List<Product> getAllProducts() {
    return productMapper.toDomainList(productRepository.findAll());
  }

  @Override
  public List<Product> getProductsByCategory(Long categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new CategoryNotFoundException(categoryId);
    }
    return productMapper.toDomainList(productRepository.findAllByCategoryId(categoryId));
  }

  @Override
  public Product getProductById(Long id) {
    return productRepository
        .findById(id)
        .map(productMapper::toDomain)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Override
  @Transactional
  public Product createProduct(Product product, Long categoryId) {
    CategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    ProductEntity entity = productMapper.toEntity(product);
    entity.setCategory(category);
    return productMapper.toDomain(productRepository.save(entity));
  }

  @Override
  @Transactional
  public Product updateProduct(Long id, Product updated, Long categoryId) {
    if (!productRepository.existsById(id)) {
      throw new ProductNotFoundException(id);
    }

    CategoryEntity category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    ProductEntity entity = productMapper.toEntity(updated);
    entity.setId(id);
    entity.setCategory(category);

    return productMapper.toDomain(productRepository.save(entity));
  }

  @Override
  @Transactional
  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }
}
