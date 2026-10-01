package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.CartMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CartServiceImpl.class})
@DisplayName("CartServiceImpl tests")
class CartServiceImplTest {

  @MockBean private CartRepository cartRepository;
  @MockBean private ProductRepository productRepository;
  @MockBean private CartMapper cartMapper;

  @Autowired private CartServiceImpl cartService;

  @Test
  @DisplayName("should return all carts")
  void getAllCartsShouldReturnList() {
    List<CartEntity> entities = List.of(new CartEntity(), new CartEntity());
    List<Cart> domains =
        List.of(
            Cart.builder().id(1L).products(List.of()).build(),
            Cart.builder().id(2L).products(List.of()).build());

    when(cartRepository.findAll()).thenReturn(entities);
    when(cartMapper.toDomainList(entities)).thenReturn(domains);

    List<Cart> result = cartService.getAllCarts();

    verify(cartRepository).findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(1).getId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("should return cart by id when exists")
  void getCartByIdShouldReturnCart() {
    CartEntity entity = new CartEntity();
    entity.setId(1L);
    Cart domain = Cart.builder().id(1L).products(List.of()).build();

    when(cartRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(cartMapper.toDomain(entity)).thenReturn(domain);

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
  @DisplayName("should create cart with valid product IDs")
  void createCartShouldSaveCart() {
    ProductEntity p1 = new ProductEntity();
    p1.setId(1L);
    p1.setName("Toy");
    ProductEntity p2 = new ProductEntity();
    p2.setId(2L);
    p2.setName("Snack");

    CartEntity savedEntity = new CartEntity();
    savedEntity.setId(10L);
    savedEntity.setProducts(List.of(p1, p2));

    Product dp1 = new Product(1L, "Toy", "Laser", 10.0, null, true);
    Product dp2 = new Product(2L, "Snack", "Tuna", 5.0, null, true);
    Cart resultDomain = new Cart(10L, List.of(dp1, dp2));

    when(productRepository.findAllById(any())).thenReturn(List.of(p1, p2));
    when(cartRepository.save(any(CartEntity.class))).thenReturn(savedEntity);
    when(cartMapper.toDomain(savedEntity)).thenReturn(resultDomain);

    Cart result = cartService.createCart(List.of(1L, 2L));

    ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
    verify(cartRepository).save(captor.capture());

    CartEntity captured = captor.getValue();
    assertThat(captured.getProducts()).containsExactlyInAnyOrder(p1, p2);

    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getProducts()).hasSize(2);
    assertThat(result.getProducts().get(0).getName()).isEqualTo("Toy");
  }

  @Test
  @DisplayName("should throw ProductNotFoundException when creating cart with invalid IDs")
  void createCartShouldThrowIfProductMissing() {
    ProductEntity p1 = new ProductEntity();
    p1.setId(1L);

    when(productRepository.findAllById(any())).thenReturn(List.of(p1));

    List<Long> productIds = List.of(1L, 2L);

    assertThatThrownBy(() -> cartService.createCart(productIds))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("Products not found for IDs");

    verify(productRepository).findAllById(any());
  }

  @Test
  @DisplayName("should update cart by adding and removing products")
  void updateCartShouldAddAndRemoveProducts() {
    ProductEntity p1 = new ProductEntity();
    p1.setId(1L);
    ProductEntity p2 = new ProductEntity();
    p2.setId(2L);
    ProductEntity p3 = new ProductEntity();
    p3.setId(3L);

    CartEntity existingCart = new CartEntity();
    existingCart.setId(5L);
    existingCart.setProducts(new ArrayList<>(List.of(p1, p2)));

    CartEntity savedCart = new CartEntity();
    savedCart.setId(5L);
    savedCart.setProducts(List.of(p2, p3));

    Cart resultDomain =
        new Cart(
            5L,
            List.of(
                new Product(2L, "Snack", null, 5.0, null, true),
                new Product(3L, "Fish", null, 12.0, null, true)));

    when(cartRepository.findById(5L)).thenReturn(Optional.of(existingCart));
    when(productRepository.findAllById(any())).thenReturn(List.of(p3));
    when(cartRepository.save(any(CartEntity.class))).thenReturn(savedCart);
    when(cartMapper.toDomain(savedCart)).thenReturn(resultDomain);

    List<Long> addIds = List.of(3L);
    List<Long> removeIds = List.of(1L);

    Cart result = cartService.updateCartContents(5L, addIds, removeIds);

    ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
    verify(cartRepository).save(captor.capture());

    CartEntity captured = captor.getValue();
    assertThat(captured.getProducts()).contains(p2, p3);
    assertThat(captured.getProducts()).doesNotContain(p1);

    assertThat(result.getProducts()).hasSize(2);
    assertThat(result.getProducts().get(0).getId()).isEqualTo(2L);
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
    CartEntity existing = new CartEntity();
    existing.setId(7L);
    existing.setProducts(new ArrayList<>());

    when(cartRepository.findById(7L)).thenReturn(Optional.of(existing));
    when(productRepository.findAllById(any())).thenReturn(List.of());

    assertThatThrownBy(() -> cartService.updateCartContents(7L, List.of(2L), null))
        .isInstanceOf(ProductNotFoundException.class)
        .hasMessageContaining("Products not found for IDs");

    verify(cartRepository).findById(7L);
    verify(productRepository).findAllById(any());
  }

  @Test
  @DisplayName("should delete cart by id")
  void deleteCartShouldCallRepository() {
    cartService.deleteCart(3L);

    verify(cartRepository).deleteById(3L);
  }
}
