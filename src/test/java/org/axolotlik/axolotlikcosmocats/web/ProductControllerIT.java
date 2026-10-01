package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.validation.CosmicWordCheck;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("ProductController integration tests")
@WithMockUser(roles = "ADMIN")
class ProductControllerIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @SpyBean private ProductService productService;

  @BeforeEach
  void setUp() {
    reset(productService);
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all products (200 OK)")
  void shouldGetAllProducts() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Food", "Space snacks", new ArrayList<>()));

    productRepository.save(new ProductEntity(null, "Snack", "Tuna cubes", 9.99, true, cat));
    productRepository.save(new ProductEntity(null, "Drink", "Meteor Cola", 5.99, true, cat));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products").isArray())
        .andExpect(jsonPath("$.products.length()").value(2))

        .andExpect(jsonPath("$.products[0].name").value("Snack"))
        .andExpect(jsonPath("$.products[0].price").value(9.99))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Food"))

        .andExpect(jsonPath("$.products[1].name").value("Drink"))
        .andExpect(jsonPath("$.products[1].price").value(5.99))
        .andExpect(jsonPath("$.products[1].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[1].category.name").value("Food"));

    verify(productService).getAllProducts();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get product by id (200 OK)")
  void shouldGetProductById() {
    var cat =
        categoryRepository.save(new CategoryEntity(null, "Toys", "Cosmic fun", new ArrayList<>()));
    var p =
        productRepository.save(
            new ProductEntity(null, "Laser Mouse", "Shiny toy", 15.5, true, cat));

    mockMvc
        .perform(get("/api/v1/products/{id}", p.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(p.getId()))
        .andExpect(jsonPath("$.name").value("Laser Mouse"))
        .andExpect(jsonPath("$.description").value("Shiny toy"))
        .andExpect(jsonPath("$.price").value(15.5))
        .andExpect(jsonPath("$.category.id").value(cat.getId()))
        .andExpect(jsonPath("$.category.name").value("Toys"))
        .andExpect(jsonPath("$.category.description").value("Cosmic fun"));

    verify(productService).getProductById(p.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when product not found")
  void shouldReturnNotFoundForMissingProduct() {
    long id = 42L;
    mockMvc
        .perform(get("/api/v1/products/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Product", id)));

    verify(productService).getProductById(id);
  }

  @Test
  @SneakyThrows
  @DisplayName("should get products by category (200 OK)")
  void shouldGetProductsByCategory() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Drinks", "Zero-G beverages", new ArrayList<>()));

    productRepository.save(new ProductEntity(null, "Cola", "Space Cola", 4.99, true, cat));
    productRepository.save(new ProductEntity(null, "Juice", "Comet Juice", 6.5, true, cat));

    mockMvc
        .perform(get("/api/v1/products").param("categoryId", String.valueOf(cat.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products").isArray())
        .andExpect(jsonPath("$.products.length()").value(2))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()));

    verify(productService).getProductsByCategory(cat.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should create product (201 Created)")
  void shouldCreateProduct() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Snacks", "Space treats", new ArrayList<>()));

    ProductRequestDto request =
        ProductRequestDto.builder()
            .name("Cosmo Fish Chips")
            .description("Cosmic tuna flavor")
            .price(7.99)
            .categoryId(cat.getId())
            .available(true)
            .build();

    mockMvc
        .perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Cosmo Fish Chips"))
        .andExpect(jsonPath("$.description").value("Cosmic tuna flavor"))
        .andExpect(jsonPath("$.price").value(7.99))
        .andExpect(jsonPath("$.category.id").value(cat.getId()))
        .andExpect(jsonPath("$.category.name").value("Snacks"));

    verify(productService).createProduct(any(), eq(cat.getId()));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return all validation errors for different invalid product requests")
  void shouldReturnAllValidationErrorsForDifferentCases() {
    ProductRequestDto nullAndBlankRequest =
        ProductRequestDto.builder()
            .name("") // @NotBlank
            .description("ordinary")
            .price(null) // @NotNull
            .categoryId(null) // @NotNull
            .available(false) // @AssertTrue
            .build();

    mockMvc
        .perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullAndBlankRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(
            jsonPath("$.errors[?(@.field == 'name')].reason")
                .value(org.hamcrest.Matchers.hasItem("must not be blank")))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'price')].reason")
                .value(org.hamcrest.Matchers.hasItem("must not be null")))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'categoryId')].reason")
                .value(org.hamcrest.Matchers.hasItem("must not be null")))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'available')].reason")
                .value(org.hamcrest.Matchers.hasItem("must be true to indicate availability")));

    ProductRequestDto cosmicAndPositiveRequest =
        ProductRequestDto.builder()
            .name("regular") // @CosmicWordCheck
            .description("ordinary")
            .price(0.0) // @DecimalMin("0.01")
            .categoryId(1L)
            .available(true)
            .build();

    mockMvc
        .perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cosmicAndPositiveRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(
            jsonPath("$.errors[?(@.field == 'name')].reason")
                .value(org.hamcrest.Matchers.hasItem(CosmicWordCheck.DEFAULT_MESSAGE)))
        .andExpect(
            jsonPath("$.errors[?(@.field == 'price')].reason")
                .value(org.hamcrest.Matchers.hasItem("must be greater than or equal to 0.01")));
  }

  @Test
  @SneakyThrows
  @DisplayName("should update product (200 OK)")
  void shouldUpdateProduct() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Supplies", "For galactic cats", new ArrayList<>()));
    var p =
        productRepository.save(new ProductEntity(null, "Old Toy", "Worn laser", 10.0, true, cat));

    ProductRequestDto update =
        ProductRequestDto.builder()
            .name("Star Toy")
            .description("Improved laser")
            .price(15.0)
            .categoryId(cat.getId())
            .available(true)
            .build();

    mockMvc
        .perform(
            put("/api/v1/products/{id}", p.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(p.getId()))
        .andExpect(jsonPath("$.name").value("Star Toy"))
        .andExpect(jsonPath("$.description").value("Improved laser"))
        .andExpect(jsonPath("$.price").value(15.0))
        .andExpect(jsonPath("$.category.id").value(cat.getId()))
        .andExpect(jsonPath("$.category.name").value("Supplies"));

    verify(productService).updateProduct(eq(p.getId()), any(), eq(cat.getId()));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when updating non-existing product")
  void shouldReturnNotFoundWhenUpdatingMissingProduct() {
    long id = 77L;
    ProductRequestDto update =
        ProductRequestDto.builder()
            .name("Nova updated")
            .description("Should fail")
            .price(1.0)
            .categoryId(1L)
            .available(true)
            .build();

    mockMvc
        .perform(
            put("/api/v1/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete product (204 No Content)")
  void shouldDeleteProduct() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Misc", "Various stuff", new ArrayList<>()));
    var p =
        productRepository.save(new ProductEntity(null, "DeleteMe", "Temp item", 2.0, true, cat));

    mockMvc.perform(delete("/api/v1/products/{id}", p.getId())).andExpect(status().isNoContent());

    verify(productService).deleteProduct(p.getId());
  }

  @Test
  @DisplayName("should return 403 Forbidden when USER tries to create product")
  @WithMockUser(roles = "USER")
  @SneakyThrows
  void shouldReturnForbiddenForUserCreate() {
    ProductRequestDto request = ProductRequestDto.builder()
            .name("Forbidden Star Item")
            .description("Desc")
            .price(10.0)
            .categoryId(1L)
            .available(true)
            .build();

    mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("should return 403 Forbidden when USER tries to delete product")
  @WithMockUser(roles = "USER")
  @SneakyThrows
  void shouldReturnForbiddenForUserDelete() {
    long productId = 123L;
    mockMvc.perform(delete("/api/v1/products/{id}", productId))
            .andExpect(status().isForbidden());
  }
}
