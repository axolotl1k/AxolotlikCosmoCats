package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ProductServiceImpl.class})
@DisplayName("ProductServiceImpl tests")
class ProductServiceImplTest {

  @MockBean private ProductRepository productRepository;
  @MockBean private CategoryRepository categoryRepository;
  @MockBean private ProductMapper productMapper;

  @Autowired private ProductServiceImpl productService;

  @Test
  @DisplayName("should return all products")
  void getAllProductsShouldReturnList() {
    ProductEntity e1 = new ProductEntity();
    e1.setId(1L);
    ProductEntity e2 = new ProductEntity();
    e2.setId(2L);
    List<ProductEntity> entities = List.of(e1, e2);

    Product p1 = new Product(1L, "Cosmo Hoodie", "Warm galaxy hoodie", 49.99, null, true);
    Product p2 = new Product(2L, "Star Mug", "Mug with constellations", 15.99, null, true);
    List<Product> domains = List.of(p1, p2);

    when(productRepository.findAll()).thenReturn(entities);
    when(productMapper.toDomainList(entities)).thenReturn(domains);

    List<Product> result = productService.getAllProducts();

    verify(productRepository).findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("Cosmo Hoodie");
    assertThat(result.get(1).getId()).isEqualTo(2L);
    assertThat(result.get(1).getName()).isEqualTo("Star Mug");
  }

  @Test
  @DisplayName("should return products filtered by category id")
  void getProductsByCategoryShouldReturnFilteredList() {
    Long catId = 1L;
    CategoryEntity catEntity = new CategoryEntity();
    catEntity.setId(catId);

    ProductEntity e1 = new ProductEntity();
    e1.setCategory(catEntity);
    ProductEntity e2 = new ProductEntity();
    e2.setCategory(catEntity);
    List<ProductEntity> entities = List.of(e1, e2);

    Category category = new Category(catId, "Clothing", "Cosmic wear");
    Product p1 = new Product(1L, "Cosmo Hoodie", "Warm galaxy hoodie", 49.99, category, true);
    Product p2 = new Product(3L, "Space Socks", "Soft socks", 9.99, category, true);
    List<Product> domains = List.of(p1, p2);

    when(categoryRepository.existsById(catId)).thenReturn(true);
    when(productRepository.findAllByCategoryId(catId)).thenReturn(entities);
    when(productMapper.toDomainList(entities)).thenReturn(domains);

    List<Product> result = productService.getProductsByCategory(catId);

    verify(categoryRepository).existsById(catId);
    verify(productRepository).findAllByCategoryId(catId);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCategory().getId()).isEqualTo(catId);
  }

  @Test
  @DisplayName("should throw CategoryNotFoundException when filtering by missing category")
  void getProductsByCategoryShouldThrowIfCategoryNotFound() {
    when(categoryRepository.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> productService.getProductsByCategory(1L))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Category", 1L));

    verify(categoryRepository).existsById(1L);
    verify(productRepository, never()).findAllByCategoryId(any());
  }

  @Test
  @DisplayName("should return product by id when exists")
  void getProductByIdShouldReturnProduct() {
    Long prodId = 5L;
    ProductEntity entity = new ProductEntity();
    entity.setId(prodId);

    Category category = new Category(2L, "Toys", "Cosmic fun");
    Product domain = new Product(prodId, "Space Cat Toy", "Laser toy", 19.99, category, true);

    when(productRepository.findById(prodId)).thenReturn(Optional.of(entity));
    when(productMapper.toDomain(entity)).thenReturn(domain);

    Product result = productService.getProductById(prodId);

    verify(productRepository).findById(prodId);
    assertThat(result.getId()).isEqualTo(prodId);
    assertThat(result.getName()).isEqualTo("Space Cat Toy");
    assertThat(result.getDescription()).isEqualTo("Laser toy");
    assertThat(result.getPrice()).isEqualTo(19.99);
    assertThat(result.getCategory()).isNotNull();
    assertThat(result.getCategory().getId()).isEqualTo(2L);
    assertThat(result.getCategory().getName()).isEqualTo("Toys");
    assertThat(result.getCategory().getDescription()).isEqualTo("Cosmic fun");
    assertThat(result.isAvailable()).isTrue();
  }

  @Test
  @DisplayName("should throw ProductNotFoundException when product not found by id")
  void getProductByIdShouldThrowIfNotFound() {
    when(productRepository.findById(9L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.getProductById(9L))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Product", 9L));

    verify(productRepository).findById(9L);
  }

  @Test
  @DisplayName("should create product with generated id and assigned category")
  void createProductShouldSetCategoryAndId() {
    Long catId = 1L;
    CategoryEntity catEntity = new CategoryEntity();
    catEntity.setId(catId);

    ProductEntity mappedEntity = new ProductEntity();
    mappedEntity.setName("Galactic Shirt");

    ProductEntity savedEntity = new ProductEntity();
    savedEntity.setId(42L);
    savedEntity.setCategory(catEntity);

    Category category = new Category(catId, "Clothing", "Cosmic wear");
    Product input = new Product(null, "Galactic Shirt", "Stylish T-shirt", 29.99, null, true);
    Product output = new Product(42L, "Galactic Shirt", "Stylish T-shirt", 29.99, category, true);

    when(categoryRepository.findById(catId)).thenReturn(Optional.of(catEntity));
    when(productMapper.toEntity(input)).thenReturn(mappedEntity);
    when(productRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(productMapper.toDomain(savedEntity)).thenReturn(output);

    Product result = productService.createProduct(input, catId);

    ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
    verify(productRepository).save(captor.capture());

    ProductEntity captured = captor.getValue();
    assertThat(captured.getCategory()).isEqualTo(catEntity);
    assertThat(captured.getName()).isEqualTo("Galactic Shirt");

    assertThat(result.getId()).isEqualTo(42L);
    assertThat(result.getCategory()).isEqualTo(category);
  }

  @Test
  @DisplayName(
      "should throw CategoryNotFoundException when creating product with invalid category id")
  void createProductShouldThrowIfCategoryNotFound() {
    when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                productService.createProduct(
                    new Product(null, "Test", null, 1.0, null, true), 999L))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Category", 999L));

    verify(categoryRepository).findById(999L);
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("should update existing product with new data and category")
  void updateProductShouldUpdateSuccessfully() {
    Long prodId = 10L;
    Long catId = 3L;

    CategoryEntity newCatEntity = new CategoryEntity();
    newCatEntity.setId(catId);
    ProductEntity mappedEntity = new ProductEntity();

    Category newCategory = new Category(catId, "Accessories", "Space items");
    Product updatedInput =
        new Product(null, "Updated Product", "Edited description", 99.99, null, true);
    Product output =
        new Product(prodId, "Updated Product", "Edited description", 99.99, newCategory, true);

    when(productRepository.existsById(prodId)).thenReturn(true);
    when(categoryRepository.findById(catId)).thenReturn(Optional.of(newCatEntity));
    when(productMapper.toEntity(updatedInput)).thenReturn(mappedEntity);
    when(productRepository.save(mappedEntity)).thenReturn(mappedEntity);
    when(productMapper.toDomain(mappedEntity)).thenReturn(output);

    Product result = productService.updateProduct(prodId, updatedInput, catId);

    ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
    verify(productRepository).save(captor.capture());

    ProductEntity captured = captor.getValue();
    assertThat(captured.getId()).isEqualTo(prodId);
    assertThat(captured.getCategory()).isEqualTo(newCatEntity);

    assertThat(result.getId()).isEqualTo(prodId);
    assertThat(result.getCategory()).isEqualTo(newCategory);
    assertThat(result.getName()).isEqualTo("Updated Product");
  }

  @Test
  @DisplayName("should throw ProductNotFoundException when updating non-existing product")
  void updateProductShouldThrowIfProductNotExists() {
    when(productRepository.existsById(999L)).thenReturn(false);

    assertThatThrownBy(() -> productService.updateProduct(999L, Product.builder().build(), 1L))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Product", 999L));

    verify(productRepository).existsById(999L);
  }

  @Test
  @DisplayName("should throw CategoryNotFoundException when updating with missing category")
  void updateProductShouldThrowIfCategoryNotFound() {
    when(productRepository.existsById(7L)).thenReturn(true);
    when(categoryRepository.findById(111L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productService.updateProduct(7L, Product.builder().build(), 111L))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Category", 111L));

    verify(productRepository).existsById(7L);
    verify(categoryRepository).findById(111L);
  }

  @Test
  @DisplayName("should delete product by id")
  void deleteProductShouldCallRepository() {
    productService.deleteProduct(15L);

    verify(productRepository).deleteById(15L);
  }
}
