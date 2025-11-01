package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;

@SpringBootTest(classes = {ProductServiceImpl.class})
@DisplayName("ProductServiceImpl tests")
class ProductServiceImplTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Test
    @DisplayName("should return all products")
    void getAllProductsShouldReturnList() {
        List<Product> products = List.of(
                new Product(1L, "Cosmo Hoodie", "Warm galaxy hoodie", 49.99, null, true),
                new Product(2L, "Star Mug", "Mug with constellations", 15.99, null, true)
        );
        when(productRepository.findAll()).thenReturn(products);

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
        Category category = new Category(1L, "Clothing", "Cosmic wear");
        Product p1 = new Product(1L, "Cosmo Hoodie", "Warm galaxy hoodie", 49.99, category, true);
        Product p2 = new Product(2L, "Star Mug", "Mug with constellations", 15.99, null, true);
        Product p3 = new Product(3L, "Space Socks", "Soft socks", 9.99, category, true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        List<Product> result = productService.getProductsByCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(productRepository).findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategory().getId()).isEqualTo(1L);
        assertThat(result.get(1).getCategory().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should throw CategoryNotFoundException when filtering by missing category")
    void getProductsByCategoryShouldThrowIfCategoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductsByCategory(1L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(String.format(ID_NOT_FOUND, "Category", 1L));

        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("should return product by id when exists")
    void getProductByIdShouldReturnProduct() {
        Category category = new Category(2L, "Toys", "Cosmic fun");
        Product product = new Product(5L, "Space Cat Toy", "Laser toy", 19.99, category, true);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(5L);

        verify(productRepository).findById(5L);

        assertThat(result.getId()).isEqualTo(5L);
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
        Category category = new Category(1L, "Clothing", "Cosmic wear");
        Product product = new Product(null, "Galactic Shirt", "Stylish T-shirt", 29.99, null, true);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.generateId()).thenReturn(42L);
        when(productRepository.save(42L, product)).thenReturn(product);

        Product result = productService.createProduct(product, 1L);

        verify(categoryRepository).findById(1L);
        verify(productRepository).generateId();
        verify(productRepository).save(42L, product);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("should throw CategoryNotFoundException when creating product with invalid category id")
    void createProductShouldThrowIfCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.createProduct(new Product(null, "Comet Cap", "Baseball cap", 15.0, null, true), 999L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(String.format(ID_NOT_FOUND, "Category", 999L));

        verify(categoryRepository).findById(999L);
    }

    @Test
    @DisplayName("should update existing product with new data and category")
    void updateProductShouldUpdateSuccessfully() {
        Category newCategory = new Category(3L, "Accessories", "Space items");
        Product updated = new Product(null, "Updated Product", "Edited description", 99.99, null, true);

        when(productRepository.existsById(10L)).thenReturn(true);
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(10L, updated)).thenReturn(updated);

        Product result = productService.updateProduct(10L, updated, 3L);

        verify(productRepository).existsById(10L);
        verify(categoryRepository).findById(3L);
        verify(productRepository).save(10L, updated);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCategory()).isEqualTo(newCategory);
        assertThat(result.getName()).isEqualTo("Updated Product");
    }

    @Test
    @DisplayName("should throw ProductNotFoundException when updating non-existing product")
    void updateProductShouldThrowIfProductNotExists() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() ->
                productService.updateProduct(999L, Product.builder().build(), 1L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(String.format(ID_NOT_FOUND, "Product", 999L));

        verify(productRepository).existsById(999L);
    }

    @Test
    @DisplayName("should throw CategoryNotFoundException when updating with missing category")
    void updateProductShouldThrowIfCategoryNotFound() {
        when(productRepository.existsById(7L)).thenReturn(true);
        when(categoryRepository.findById(111L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.updateProduct(7L, Product.builder().build(), 111L))
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
