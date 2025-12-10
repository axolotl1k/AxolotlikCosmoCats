package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartUpdateRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.validation.AtLeastOneNonEmpty;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.CartService;
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
import java.util.List;

import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("CartController integration tests")
@WithMockUser(roles = "ADMIN")
class CartControllerIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CartRepository cartRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @SpyBean private CartService cartService;

  @BeforeEach
  void setUp() {
    reset(cartService);
    cartRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all carts (200 OK)")
  void shouldGetAllCarts() {
    var category =
        categoryRepository.save(
            new CategoryEntity(null, "Food", "Space snacks", new ArrayList<>()));

    var p1 =
        productRepository.save(
            new ProductEntity(null, "Cosmic Tuna", "Tasty", 5.0, true, category));
    var p2 =
        productRepository.save(
            new ProductEntity(null, "Star Drink", "Energy", 3.0, true, category));

    cartRepository.save(new CartEntity(null, List.of(p1, p2)));
    cartRepository.save(new CartEntity(null, List.of(p2)));

    mockMvc
        .perform(get("/api/v1/carts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.carts").isArray())
        .andExpect(jsonPath("$.carts.length()").value(2))
        .andExpect(jsonPath("$.carts[0].products.length()").value(2))
        .andExpect(jsonPath("$.carts[0].products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.carts[0].products[0].name").value("Cosmic Tuna"))
        .andExpect(jsonPath("$.carts[0].products[0].description").value("Tasty"))
        .andExpect(jsonPath("$.carts[0].products[0].price").value(5.0))
        .andExpect(jsonPath("$.carts[0].products[0].available").value(true))
        .andExpect(jsonPath("$.carts[0].products[0].category.id").value(category.getId()))
        .andExpect(jsonPath("$.carts[0].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.carts[0].products[0].category.description").value("Space snacks"))

        .andExpect(jsonPath("$.carts[1].products.length()").value(1))
        .andExpect(jsonPath("$.carts[1].products[0].id").value(p2.getId()))
        .andExpect(jsonPath("$.carts[1].products[0].name").value("Star Drink"))
        .andExpect(jsonPath("$.carts[1].products[0].description").value("Energy"))
        .andExpect(jsonPath("$.carts[1].products[0].price").value(3.0))
        .andExpect(jsonPath("$.carts[1].products[0].available").value(true))
        .andExpect(jsonPath("$.carts[1].products[0].category.id").value(category.getId()))
        .andExpect(jsonPath("$.carts[1].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.carts[1].products[0].category.description").value("Space snacks"));

    verify(cartService).getAllCarts();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get cart by id (200 OK)")
  void shouldGetCartById() {
    var category =
        categoryRepository.save(
            new CategoryEntity(null, "Supplies", "Space tools", new ArrayList<>()));
    var p1 =
        productRepository.save(
            new ProductEntity(null, "Cosmic Wrench", "Tools for ships", 20.0, true, category));
    var cart = cartRepository.save(new CartEntity(null, List.of(p1)));

    mockMvc
        .perform(get("/api/v1/carts/{id}", cart.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(cart.getId()))
        .andExpect(jsonPath("$.products.length()").value(1))
        .andExpect(jsonPath("$.products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Cosmic Wrench"))
        .andExpect(jsonPath("$.products[0].description").value("Tools for ships"))
        .andExpect(jsonPath("$.products[0].price").value(20.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(category.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Supplies"))
        .andExpect(jsonPath("$.products[0].category.description").value("Space tools"));

    verify(cartService).getCartById(cart.getId());
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

    verify(cartService).getCartById(id);
  }

  @Test
  @SneakyThrows
  @DisplayName("should create cart (201 Created)")
  void shouldCreateCart() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Food", "Space snacks", new ArrayList<>()));
    var p1 =
        productRepository.save(
            new ProductEntity(null, "Cosmic Fish", "Star flavor", 10.0, true, cat));
    var p2 =
        productRepository.save(
            new ProductEntity(null, "Galaxy Snack", "Energy bites", 5.0, true, cat));

    CartRequestDto request =
        CartRequestDto.builder().productIds(List.of(p1.getId(), p2.getId())).build();

    mockMvc
        .perform(
            post("/api/v1/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.products.length()").value(2))
        .andExpect(jsonPath("$.products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Cosmic Fish"))
        .andExpect(jsonPath("$.products[0].description").value("Star flavor"))
        .andExpect(jsonPath("$.products[0].price").value(10.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.products[1].id").value(p2.getId()))
        .andExpect(jsonPath("$.products[1].name").value("Galaxy Snack"))
        .andExpect(jsonPath("$.products[1].description").value("Energy bites"))
        .andExpect(jsonPath("$.products[1].price").value(5.0))
        .andExpect(jsonPath("$.products[1].available").value(true))
        .andExpect(jsonPath("$.products[1].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[1].category.name").value("Food"));

    verify(cartService).createCart(any());
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
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Supplies", "Space tools", new ArrayList<>()));

    var p1 = productRepository.save(new ProductEntity(null, "Tool A", "Basic", 5.0, true, cat));
    var p2 = productRepository.save(new ProductEntity(null, "Tool B", "Better", 6.0, true, cat));
    var p3 = productRepository.save(new ProductEntity(null, "Tool C", "Best", 7.0, true, cat));

    var cart = cartRepository.save(new CartEntity(null, List.of(p1, p2)));

    CartUpdateRequestDto update =
        CartUpdateRequestDto.builder()
            .addProductIds(List.of(p3.getId()))
            .removeProductIds(List.of(p1.getId()))
            .build();

    mockMvc
        .perform(
            patch("/api/v1/carts/{id}", cart.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(cart.getId()))
        .andExpect(jsonPath("$.products.length()").value(2))
        .andExpect(jsonPath("$.products[0].id").value(p2.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Tool B"))
        .andExpect(jsonPath("$.products[0].description").value("Better"))
        .andExpect(jsonPath("$.products[0].price").value(6.0))
        .andExpect(jsonPath("$.products[1].id").value(p3.getId()))
        .andExpect(jsonPath("$.products[1].name").value("Tool C"))
        .andExpect(jsonPath("$.products[1].description").value("Best"))
        .andExpect(jsonPath("$.products[1].price").value(7.0));

    verify(cartService).updateCartContents(eq(cart.getId()), any(), any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 400 when no add/remove product ids provided")
  void shouldReturnBadRequestForEmptyUpdateRequest() {
    var cart = cartRepository.save(new CartEntity(null, new ArrayList<>()));

    CartUpdateRequestDto invalid =
        CartUpdateRequestDto.builder().addProductIds(null).removeProductIds(null).build();

    mockMvc
        .perform(
            patch("/api/v1/carts/{id}", cart.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors[0].reason").value(AtLeastOneNonEmpty.DEFAULT_MESSAGE));
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

    verify(cartService).updateCartContents(eq(55L), any(), any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete cart (204 No Content)")
  void shouldDeleteCart() {
    var cat =
        categoryRepository.save(new CategoryEntity(null, "Supplies", "Tools", new ArrayList<>()));
    var p1 =
        productRepository.save(new ProductEntity(null, "Old Tool", "Remove me", 4.0, true, cat));

    var cart = cartRepository.save(new CartEntity(null, List.of(p1)));

    mockMvc.perform(delete("/api/v1/carts/{id}", cart.getId())).andExpect(status().isNoContent());

    verify(cartService).deleteCart(cart.getId());
  }
}
