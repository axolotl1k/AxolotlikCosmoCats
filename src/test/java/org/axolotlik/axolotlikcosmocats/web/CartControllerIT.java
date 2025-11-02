package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartUpdateRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.validation.AtLeastOneNonEmpty;
import org.axolotlik.axolotlikcosmocats.repository.impl.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CartController integration tests")
class CartControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CartRepository cartRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @SpyBean private CartService cartService;

  @BeforeEach
  void setUp() {
    reset(cartService);
    cartRepository.clear();
    productRepository.clear();
    categoryRepository.clear();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all carts (200 OK)")
  void shouldGetAllCarts() {
    Category category = new Category(1L, "Food", "Space snacks");
    categoryRepository.save(1L, category);

    Product p1 = new Product(1L, "Cosmic Tuna", "Tasty", 5.0, category, true);
    Product p2 = new Product(2L, "Star Drink", "Energy", 3.0, category, true);
    productRepository.save(1L, p1);
    productRepository.save(2L, p2);

    cartRepository.save(1L, new Cart(1L, List.of(p1, p2)));
    cartRepository.save(2L, new Cart(2L, List.of(p2)));

    mockMvc
        .perform(get("/api/v1/carts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.carts").isArray())
        .andExpect(jsonPath("$.carts.length()").value(2))

        .andExpect(jsonPath("$.carts[0].id").value(1))
        .andExpect(jsonPath("$.carts[0].products.length()").value(2))
        .andExpect(jsonPath("$.carts[0].products[0].id").value(1))
        .andExpect(jsonPath("$.carts[0].products[0].name").value("Cosmic Tuna"))
        .andExpect(jsonPath("$.carts[0].products[0].description").value("Tasty"))
        .andExpect(jsonPath("$.carts[0].products[0].price").value(5.0))
        .andExpect(jsonPath("$.carts[0].products[0].available").value(true))
        .andExpect(jsonPath("$.carts[0].products[0].category.id").value(1))
        .andExpect(jsonPath("$.carts[0].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.carts[0].products[0].category.description").value("Space snacks"))

        .andExpect(jsonPath("$.carts[1].id").value(2))
        .andExpect(jsonPath("$.carts[1].products.length()").value(1))
        .andExpect(jsonPath("$.carts[1].products[0].id").value(2))
        .andExpect(jsonPath("$.carts[1].products[0].name").value("Star Drink"))
        .andExpect(jsonPath("$.carts[1].products[0].description").value("Energy"))
        .andExpect(jsonPath("$.carts[1].products[0].price").value(3.0))
        .andExpect(jsonPath("$.carts[1].products[0].available").value(true))
        .andExpect(jsonPath("$.carts[1].products[0].category.id").value(1))
        .andExpect(jsonPath("$.carts[1].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.carts[1].products[0].category.description").value("Space snacks"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should get cart by id (200 OK)")
  void shouldGetCartById() {
    Category category = new Category(1L, "Supplies", "Space tools");
    categoryRepository.save(1L, category);
    Product p1 = new Product(1L, "Cosmic Wrench", "Tools for ships", 20.0, category, true);
    productRepository.save(1L, p1);

    Cart cart = new Cart(1L, List.of(p1));
    cartRepository.save(1L, cart);

    mockMvc
        .perform(get("/api/v1/carts/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.products.length()").value(1))
        .andExpect(jsonPath("$.products[0].id").value(1))
        .andExpect(jsonPath("$.products[0].name").value("Cosmic Wrench"))
        .andExpect(jsonPath("$.products[0].description").value("Tools for ships"))
        .andExpect(jsonPath("$.products[0].price").value(20.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(1))
        .andExpect(jsonPath("$.products[0].category.name").value("Supplies"))
        .andExpect(jsonPath("$.products[0].category.description").value("Space tools"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when cart not found")
  void shouldReturnNotFoundForMissingCart() {
    long id = 99L;
    mockMvc
        .perform(get("/api/v1/carts/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Cart", id)));
  }

  @Test
  @SneakyThrows
  @DisplayName("should create cart (201 Created)")
  void shouldCreateCart() {
    Category cat = new Category(1L, "Food", "Space snacks");
    categoryRepository.save(1L, cat);
    Product p1 = new Product(1L, "Cosmic Fish", "Star flavor", 10.0, cat, true);
    Product p2 = new Product(2L, "Galaxy Snack", "Energy bites", 5.0, cat, true);
    productRepository.save(1L, p1);
    productRepository.save(2L, p2);

    CartRequestDto request = CartRequestDto.builder().productIds(List.of(1L, 2L)).build();

    mockMvc
        .perform(
            post("/api/v1/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.products.length()").value(2))

        .andExpect(jsonPath("$.products[0].id").value(1))
        .andExpect(jsonPath("$.products[0].name").value("Cosmic Fish"))
        .andExpect(jsonPath("$.products[0].description").value("Star flavor"))
        .andExpect(jsonPath("$.products[0].price").value(10.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(1))
        .andExpect(jsonPath("$.products[0].category.name").value("Food"))

        .andExpect(jsonPath("$.products[1].id").value(2))
        .andExpect(jsonPath("$.products[1].name").value("Galaxy Snack"))
        .andExpect(jsonPath("$.products[1].description").value("Energy bites"))
        .andExpect(jsonPath("$.products[1].price").value(5.0))
        .andExpect(jsonPath("$.products[1].available").value(true))
        .andExpect(jsonPath("$.products[1].category.id").value(1))
        .andExpect(jsonPath("$.products[1].category.name").value("Food"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 400 for empty product list")
  void shouldReturnBadRequestForEmptyProductList() {
    CartRequestDto invalid = CartRequestDto.builder().productIds(List.of()).build();

    mockMvc
        .perform(
            post("/api/v1/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors[0].field").value("productIds"))
        .andExpect(jsonPath("$.errors[0].reason").value("must not be empty"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should update cart contents (200 OK)")
  void shouldUpdateCart() {
    Category cat = new Category(1L, "Supplies", "Space tools");
    categoryRepository.save(1L, cat);
    Product p1 = new Product(1L, "Tool A", "Basic", 5.0, cat, true);
    Product p2 = new Product(2L, "Tool B", "Better", 6.0, cat, true);
    Product p3 = new Product(3L, "Tool C", "Best", 7.0, cat, true);
    productRepository.save(1L, p1);
    productRepository.save(2L, p2);
    productRepository.save(3L, p3);

    Cart cart = new Cart(1L, List.of(p1, p2));
    cartRepository.save(1L, cart);

    CartUpdateRequestDto update =
        CartUpdateRequestDto.builder()
            .addProductIds(List.of(3L))
            .removeProductIds(List.of(1L))
            .build();

    mockMvc
        .perform(
            patch("/api/v1/carts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.products.length()").value(2))
        .andExpect(jsonPath("$.products[0].id").value(2))
        .andExpect(jsonPath("$.products[0].name").value("Tool B"))
        .andExpect(jsonPath("$.products[0].description").value("Better"))
        .andExpect(jsonPath("$.products[0].price").value(6.0))
        .andExpect(jsonPath("$.products[1].id").value(3))
        .andExpect(jsonPath("$.products[1].name").value("Tool C"))
        .andExpect(jsonPath("$.products[1].description").value("Best"))
        .andExpect(jsonPath("$.products[1].price").value(7.0));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 400 when no add/remove product ids provided")
  void shouldReturnBadRequestForEmptyUpdateRequest() {
    CartUpdateRequestDto invalid =
        CartUpdateRequestDto.builder().addProductIds(null).removeProductIds(null).build();

    mockMvc
        .perform(
            patch("/api/v1/carts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(
            jsonPath("$.errors[0].reason")
                .value(AtLeastOneNonEmpty.DEFAULT_MESSAGE));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when updating non-existing cart")
  void shouldReturnNotFoundWhenUpdatingMissingCart() {
    CartUpdateRequestDto update = CartUpdateRequestDto.builder().addProductIds(List.of(1L)).build();

    mockMvc
        .perform(
            patch("/api/v1/carts/{id}", 55L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Cart", 55L)));
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete cart (204 No Content)")
  void shouldDeleteCart() {
    Category cat = new Category(1L, "Supplies", "Tools");
    categoryRepository.save(1L, cat);
    Product p1 = new Product(1L, "Old Tool", "Remove me", 4.0, cat, true);
    productRepository.save(1L, p1);

    Cart cart = new Cart(1L, List.of(p1));
    cartRepository.save(1L, cart);

    mockMvc.perform(delete("/api/v1/carts/{id}", 1L)).andExpect(status().isNoContent());
  }
}
