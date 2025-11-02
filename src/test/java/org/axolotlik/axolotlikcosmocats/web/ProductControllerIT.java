package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.dto.product.ProductRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.validation.CosmicWordCheck;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductController integration tests")
class ProductControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @SpyBean private ProductService productService;

  @BeforeEach
  void setUp() {
    reset(productService);
    productRepository.clear();
    categoryRepository.clear();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all products (200 OK)")
  void shouldGetAllProducts() {
    Category category = new Category(1L, "Food", "Space snacks");
    categoryRepository.save(1L, category);

    productRepository.save(1L, new Product(1L, "Snack", "Tuna cubes", 9.99, category, true));
    productRepository.save(2L, new Product(2L, "Drink", "Meteor Cola", 5.99, category, true));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products").isArray())
        .andExpect(jsonPath("$.products.length()").value(2))

        .andExpect(jsonPath("$.products[0].name").value("Snack"))
        .andExpect(jsonPath("$.products[0].price").value(9.99))
        .andExpect(jsonPath("$.products[0].category.id").value(1))
        .andExpect(jsonPath("$.products[0].category.name").value("Food"))

        .andExpect(jsonPath("$.products[1].name").value("Drink"))
        .andExpect(jsonPath("$.products[1].price").value(5.99))
        .andExpect(jsonPath("$.products[1].category.id").value(1))
        .andExpect(jsonPath("$.products[1].category.name").value("Food"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should get product by id (200 OK)")
  void shouldGetProductById() {
    Category category = new Category(1L, "Toys", "Cosmic fun");
    categoryRepository.save(1L, category);

    Product product = new Product(1L, "Laser Mouse", "Shiny toy", 15.5, category, true);
    productRepository.save(1L, product);

    mockMvc
        .perform(get("/api/v1/products/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Laser Mouse"))
        .andExpect(jsonPath("$.description").value("Shiny toy"))
        .andExpect(jsonPath("$.price").value(15.5))
        .andExpect(jsonPath("$.category.id").value(1))
        .andExpect(jsonPath("$.category.name").value("Toys"))
        .andExpect(jsonPath("$.category.description").value("Cosmic fun"));
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
  }

  @Test
  @SneakyThrows
  @DisplayName("should get products by category (200 OK)")
  void shouldGetProductsByCategory() {
    Category category = new Category(1L, "Drinks", "Zero-G beverages");
    categoryRepository.save(1L, category);

    productRepository.save(1L, new Product(1L, "Cola", "Space Cola", 4.99, category, true));
    productRepository.save(2L, new Product(2L, "Juice", "Comet Juice", 6.5, category, true));

    mockMvc
        .perform(get("/api/v1/products").param("categoryId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products").isArray())
        .andExpect(jsonPath("$.products.length()").value(2))

        .andExpect(jsonPath("$.products[0].id").value(1))
        .andExpect(jsonPath("$.products[0].name").value("Cola"))
        .andExpect(jsonPath("$.products[0].description").value("Space Cola"))
        .andExpect(jsonPath("$.products[0].price").value(4.99))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(1))
        .andExpect(jsonPath("$.products[0].category.name").value("Drinks"))
        .andExpect(jsonPath("$.products[0].category.description").value("Zero-G beverages"))

        .andExpect(jsonPath("$.products[1].id").value(2))
        .andExpect(jsonPath("$.products[1].name").value("Juice"))
        .andExpect(jsonPath("$.products[1].description").value("Comet Juice"))
        .andExpect(jsonPath("$.products[1].price").value(6.5))
        .andExpect(jsonPath("$.products[1].available").value(true))
        .andExpect(jsonPath("$.products[1].category.id").value(1))
        .andExpect(jsonPath("$.products[1].category.name").value("Drinks"))
        .andExpect(jsonPath("$.products[1].category.description").value("Zero-G beverages"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should create product (201 Created)")
  void shouldCreateProduct() {
    Category category = new Category(1L, "Snacks", "Space treats");
    categoryRepository.save(1L, category);

    ProductRequestDto request =
        ProductRequestDto.builder()
            .name("Cosmo Fish Chips")
            .description("Cosmic tuna flavor")
            .price(7.99)
            .categoryId(1L)
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
        .andExpect(jsonPath("$.category.id").value(1))
        .andExpect(jsonPath("$.category.name").value("Snacks"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return all validation errors for different invalid product requests")
  void shouldReturnAllValidationErrorsForDifferentCases() {

    // Перший об'єкт: перевіряємо базові NotNull / NotBlank / AssertTrue
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

    // Другий об'єкт: перевіряємо позитивні числа і cosmic word check
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
    Category category = new Category(1L, "Supplies", "For galactic cats");
    categoryRepository.save(1L, category);
    productRepository.save(1L, new Product(1L, "Old Toy", "Worn laser", 10.0, category, true));

    ProductRequestDto update =
        ProductRequestDto.builder()
            .name("Star Toy")
            .description("Improved laser")
            .price(15.0)
            .categoryId(1L)
            .available(true)
            .build();

    mockMvc
        .perform(
            put("/api/v1/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Star Toy"))
        .andExpect(jsonPath("$.description").value("Improved laser"))
        .andExpect(jsonPath("$.price").value(15.0))
        .andExpect(jsonPath("$.category.id").value(1))
        .andExpect(jsonPath("$.category.name").value("Supplies"));
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
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Product", id)));
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete product (204 No Content)")
  void shouldDeleteProduct() {
    Category category = new Category(1L, "Misc", "Various stuff");
    categoryRepository.save(1L, category);
    productRepository.save(1L, new Product(1L, "DeleteMe", "Temp item", 2.0, category, true));

    mockMvc.perform(delete("/api/v1/products/{id}", 1L)).andExpect(status().isNoContent());
  }
}
