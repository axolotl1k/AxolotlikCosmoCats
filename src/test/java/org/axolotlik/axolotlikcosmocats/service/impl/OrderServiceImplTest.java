package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.impl.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.OrderRepository;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.OrderNotFoundException;
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

@SpringBootTest(classes = {OrderServiceImpl.class})
@DisplayName("OrderServiceImpl tests")
class OrderServiceImplTest {

  @MockBean private OrderRepository orderRepository;

  @MockBean private CartRepository cartRepository;

  @Autowired private OrderServiceImpl orderService;

  @Test
  @DisplayName("should return all orders")
  void getAllOrdersShouldReturnList() {
    List<Order> orders =
        List.of(
            Order.builder().id(1L).status(OrderStatus.NEW).build(),
            Order.builder().id(2L).status(OrderStatus.PROCESSING).build());
    when(orderRepository.findAll()).thenReturn(orders);

    List<Order> result = orderService.getAllOrders();

    verify(orderRepository).findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(1).getId()).isEqualTo(2L);
    assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.NEW);
    assertThat(result.get(1).getStatus()).isEqualTo(OrderStatus.PROCESSING);
  }

  @Test
  @DisplayName("should return order by id when exists")
  void getOrderByIdShouldReturnOrder() {
    Order order = Order.builder().id(5L).status(OrderStatus.NEW).totalPrice(100.0).build();
    when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

    Order result = orderService.getOrderById(5L);

    verify(orderRepository).findById(5L);

    assertThat(result.getId()).isEqualTo(5L);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.NEW);
    assertThat(result.getTotalPrice()).isEqualTo(100.0);
  }

  @Test
  @DisplayName("should throw OrderNotFoundException when order not found")
  void getOrderByIdShouldThrowIfNotFound() {
    when(orderRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.getOrderById(99L))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Order", 99L));

    verify(orderRepository).findById(99L);
  }

  @Test
  @DisplayName("should create order from existing cart and preserve product categories")
  void createOrderFromCartShouldWorkProperly() {
    Category toys = new Category(1L, "Toys", "Cosmic entertainment");
    Category food = new Category(2L, "Food", "Space snacks");

    Product p1 = new Product(1L, "Toy", "Laser", 10.0, toys, true);
    Product p2 = new Product(2L, "Snack", "Tuna", 5.0, food, true);

    Cart cart = Cart.builder().id(3L).products(List.of(p1, p2)).build();

    when(cartRepository.findById(3L)).thenReturn(Optional.of(cart));
    when(orderRepository.generateId()).thenReturn(10L);
    when(orderRepository.save(eq(10L), any(Order.class))).thenAnswer(inv -> inv.getArgument(1));

    Order result = orderService.createOrderFromCart(3L);

    verify(cartRepository).findById(3L);
    verify(orderRepository).generateId();
    verify(cartRepository).deleteById(3L);
    verify(orderRepository).save(eq(10L), any(Order.class));

    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getProducts()).hasSize(2);
    assertThat(result.getTotalPrice()).isEqualTo(15.0);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.NEW);

    List<Product> sorted =
        result.getProducts().stream()
            .sorted(java.util.Comparator.comparing(Product::getId))
            .toList();

    Product productA = sorted.get(0);
    Product productB = sorted.get(1);

    assertThat(productA.getId()).isEqualTo(1L);
    assertThat(productA.getName()).isEqualTo("Toy");
    assertThat(productA.getDescription()).isEqualTo("Laser");
    assertThat(productA.getPrice()).isEqualTo(10.0);
    assertThat(productA.isAvailable()).isTrue();
    assertThat(productA.getCategory()).isNotNull();
    assertThat(productA.getCategory().getId()).isEqualTo(1L);
    assertThat(productA.getCategory().getName()).isEqualTo("Toys");
    assertThat(productA.getCategory().getDescription()).isEqualTo("Cosmic entertainment");

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
  @DisplayName("should throw CartNotFoundException when creating order from missing cart")
  void createOrderFromCartShouldThrowIfCartMissing() {
    when(cartRepository.findById(9L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.createOrderFromCart(9L))
        .isInstanceOf(CartNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Cart", 9L));

    verify(cartRepository).findById(9L);
  }

  @Test
  @DisplayName("should update order status successfully")
  void updateOrderStatusShouldChangeStatus() {
    Order existing = Order.builder().id(1L).status(OrderStatus.NEW).totalPrice(50.0).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(orderRepository.save(eq(1L), any(Order.class))).thenAnswer(inv -> inv.getArgument(1));

    Order result = orderService.updateOrderStatus(1L, "Processing");

    verify(orderRepository).findById(1L);
    verify(orderRepository).save(eq(1L), any(Order.class));

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
  }

  @Test
  @DisplayName("should throw OrderNotFoundException when updating missing order")
  void updateOrderStatusShouldThrowIfOrderMissing() {
    when(orderRepository.findById(100L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.updateOrderStatus(100L, "Completed"))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessage(String.format(ID_NOT_FOUND, "Order", 100L));

    verify(orderRepository).findById(100L);
  }

  @Test
  @DisplayName("should delete order by id")
  void deleteOrderShouldCallRepository() {
    orderService.deleteOrder(2L);

    verify(orderRepository).deleteById(2L);
  }
}
