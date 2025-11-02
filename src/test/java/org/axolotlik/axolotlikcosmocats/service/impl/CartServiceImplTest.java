package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.impl.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
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

@SpringBootTest(classes = {CartServiceImpl.class})
@DisplayName("CartServiceImpl tests")
class CartServiceImplTest {

  @MockBean private CartRepository cartRepository;

  @MockBean private ProductRepository productRepository;

  @Autowired private CartServiceImpl cartService;

  @Test
  @DisplayName("should return all carts")
  void getAllCartsShouldReturnList() {
    List<Cart> carts =
        List.of(
            Cart.builder().id(1L).products(List.of()).build(),
            Cart.builder().id(2L).products(List.of()).build());
    when(cartRepository.findAll()).thenReturn(carts);

    List<Cart> result = cartService.getAllCarts();

    verify(cartRepository).findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(1).getId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("should return cart by id when exists")
  void getCartByIdShouldReturnCart() {
    Cart cart = Cart.builder().id(1L).products(List.of()).build();
    when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

    Cart result = cartService.getCartById(1L);

    verify(cartRepository).findById(1L);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getProducts()).isEmpty();
  }

  @Test
  @DisplayName("should throw CartNotFoundException when cart not found")
  void getCartByIdShouldThrowIfNotFound() {
    when(cartRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cartService.getCartById(99L))
        .isInstanceOf(CartNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Cart", 99L));

    verify(cartRepository).findById(99L);
  }

  @Test
  @DisplayName("should create cart with valid product IDs and product categories")
  void createCartShouldSaveCart() {
    Category toys = new Category(1L, "Toys", "Cosmic fun");
    Category food = new Category(2L, "Food", "Space snacks");

    Product p1 = new Product(1L, "Toy", "Laser", 10.0, toys, true);
    Product p2 = new Product(2L, "Snack", "Tuna", 5.0, food, true);
    List<Long> productIds = List.of(1L, 2L);

    when(productRepository.findAll()).thenReturn(List.of(p1, p2));
    when(cartRepository.generateId()).thenReturn(10L);
    when(cartRepository.save(eq(10L), any(Cart.class))).thenAnswer(inv -> inv.getArgument(1));

    Cart result = cartService.createCart(productIds);

    verify(productRepository).findAll();
    verify(cartRepository).generateId();
    verify(cartRepository).save(eq(10L), any(Cart.class));

    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getProducts()).hasSize(2);

    List<Product> products =
        result.getProducts().stream()
            .sorted(java.util.Comparator.comparing(Product::getId))
            .toList();

    Product productA = products.get(0);
    Product productB = products.get(1);

    assertThat(productA.getId()).isEqualTo(1L);
    assertThat(productA.getName()).isEqualTo("Toy");
    assertThat(productA.getDescription()).isEqualTo("Laser");
    assertThat(productA.getPrice()).isEqualTo(10.0);
    assertThat(productA.isAvailable()).isTrue();
    assertThat(productA.getCategory()).isNotNull();
    assertThat(productA.getCategory().getId()).isEqualTo(1L);
    assertThat(productA.getCategory().getName()).isEqualTo("Toys");
    assertThat(productA.getCategory().getDescription()).isEqualTo("Cosmic fun");

    assertThat(productB.getId()).isEqualTo(2L);
    assertThat(productB.getName()).isEqualTo("Snack");
    assertThat(productB.getDescription()).isEqualTo("Tuna");
    assertThat(productB.getPrice()).isEqualTo(5.0);
    assertThat(productB.isAvailable()).isTrue();
    assertThat(productB.getCategory()).isNotNull();
    assertThat(productB.getCategory().getId()).isEqualTo(2L);
    assertThat(productB.getCategory().getName()).isEqualTo("Food");
    assertThat(productB.getCategory().getDescription()).isEqualTo("Space snacks");
  }

  @Test
  @DisplayName("should throw ProductNotFoundException when creating cart with invalid IDs")
  void createCartShouldThrowIfProductMissing() {
    Product p1 = new Product(1L, "Toy", "Laser", 10.0, null, true);
    when(productRepository.findAll()).thenReturn(List.of(p1));

    List<Long> productIds = List.of(1L, 2L);

    assertThatThrownBy(() -> cartService.createCart(productIds))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("One or more products not found");

    verify(productRepository).findAll();
  }

  @Test
  @DisplayName("should update cart by adding and removing products and keep categories")
  void updateCartShouldAddAndRemoveProducts() {
    Category toys = new Category(1L, "Toys", "Cosmic fun");
    Category food = new Category(2L, "Food", "Space snacks");
    Category fish = new Category(3L, "Fish", "Deep space sushi");

    Product p1 = new Product(1L, "Toy", "Laser", 10.0, toys, true);
    Product p2 = new Product(2L, "Snack", "Tuna", 5.0, food, true);
    Product p3 = new Product(3L, "Fish", "Salmon", 12.0, fish, true);

    Cart existing = Cart.builder().id(5L).products(List.of(p1, p2)).build();

    when(cartRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(productRepository.findAll()).thenReturn(List.of(p1, p2, p3));
    when(cartRepository.save(eq(5L), any(Cart.class))).thenAnswer(inv -> inv.getArgument(1));

    List<Long> addIds = List.of(3L);
    List<Long> removeIds = List.of(1L);

    Cart result = cartService.updateCartContents(5L, addIds, removeIds);

    verify(cartRepository).findById(5L);
    verify(productRepository).findAll();
    verify(cartRepository).save(eq(5L), any(Cart.class));

    List<Product> products =
        result.getProducts().stream()
            .sorted(java.util.Comparator.comparing(Product::getId))
            .toList();

    assertThat(products).hasSize(2);

    Product productA = products.get(0);
    Product productB = products.get(1);

    assertThat(productA.getId()).isEqualTo(2L);
    assertThat(productA.getName()).isEqualTo("Snack");
    assertThat(productA.getDescription()).isEqualTo("Tuna");
    assertThat(productA.getPrice()).isEqualTo(5.0);
    assertThat(productA.isAvailable()).isTrue();
    assertThat(productA.getCategory()).isNotNull();
    assertThat(productA.getCategory().getId()).isEqualTo(2L);
    assertThat(productA.getCategory().getName()).isEqualTo("Food");
    assertThat(productA.getCategory().getDescription()).isEqualTo("Space snacks");

    assertThat(productB.getId()).isEqualTo(3L);
    assertThat(productB.getName()).isEqualTo("Fish");
    assertThat(productB.getDescription()).isEqualTo("Salmon");
    assertThat(productB.getPrice()).isEqualTo(12.0);
    assertThat(productB.isAvailable()).isTrue();
    assertThat(productB.getCategory()).isNotNull();
    assertThat(productB.getCategory().getId()).isEqualTo(3L);
    assertThat(productB.getCategory().getName()).isEqualTo("Fish");
    assertThat(productB.getCategory().getDescription()).isEqualTo("Deep space sushi");
  }

  @Test
  @DisplayName("should throw CartNotFoundException when updating non-existing cart")
  void updateCartShouldThrowIfCartMissing() {
    when(cartRepository.findById(50L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cartService.updateCartContents(50L, List.of(1L), null))
        .isInstanceOf(CartNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Cart", 50L));

    verify(cartRepository).findById(50L);
  }

  @Test
  @DisplayName("should throw ProductNotFoundException when adding missing products")
  void updateCartShouldThrowIfProductMissing() {
    Product p1 = new Product(1L, "Toy", "Laser", 10.0, null, true);
    Cart existing = Cart.builder().id(7L).products(List.of(p1)).build();

    when(cartRepository.findById(7L)).thenReturn(Optional.of(existing));
    when(productRepository.findAll()).thenReturn(List.of(p1));

    assertThatThrownBy(() -> cartService.updateCartContents(7L, List.of(2L), null))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("One or more products not found");

    verify(cartRepository).findById(7L);
    verify(productRepository).findAll();
  }

  @Test
  @DisplayName("should delete cart by id")
  void deleteCartShouldCallRepository() {
    cartService.deleteCart(3L);

    verify(cartRepository).deleteById(3L);
  }
}
