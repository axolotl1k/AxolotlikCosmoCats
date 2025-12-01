package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.OrderRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.OrderEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.repository.projection.ProductSalesStats;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.OrderNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {OrderServiceImpl.class})
@DisplayName("OrderServiceImpl tests")
class OrderServiceImplTest {

  @MockBean private OrderRepository orderRepository;

  @MockBean private CartRepository cartRepository;
  @MockBean private OrderMapper orderMapper;

  @Autowired private OrderServiceImpl orderService;

  @Test
  @DisplayName("should return all orders")
  void getAllOrdersShouldReturnList() {
    List<OrderEntity> entities = List.of(new OrderEntity(), new OrderEntity());
    List<Order> domains =
        List.of(
            Order.builder().id(1L).status(OrderStatus.NEW).build(),
            Order.builder().id(2L).status(OrderStatus.PROCESSING).build());

    when(orderRepository.findAll()).thenReturn(entities);
    when(orderMapper.toDomainList(entities)).thenReturn(domains);

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
    OrderEntity entity = new OrderEntity();
    entity.setId(5L);
    Order domain = Order.builder().id(5L).status(OrderStatus.NEW).totalPrice(100.0).build();

    when(orderRepository.findById(5L)).thenReturn(Optional.of(entity));
    when(orderMapper.toDomain(entity)).thenReturn(domain);

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
  @DisplayName("should create order from existing cart")
  void createOrderFromCartShouldWorkProperly() {
    ProductEntity p1 = new ProductEntity();
    p1.setId(1L);
    p1.setPrice(10.0);
    ProductEntity p2 = new ProductEntity();
    p2.setId(2L);
    p2.setPrice(5.0);

    CartEntity cartEntity = new CartEntity();
    cartEntity.setId(3L);
    cartEntity.setProducts(List.of(p1, p2));

    OrderEntity savedOrderEntity = new OrderEntity();
    savedOrderEntity.setId(10L);

    Product dp1 = new Product(1L, "Toy", "Laser", 10.0, null, true);
    Product dp2 = new Product(2L, "Snack", "Tuna", 5.0, null, true);
    Order resultDomain =
        Order.builder()
            .id(10L)
            .products(List.of(dp1, dp2))
            .totalPrice(15.0)
            .status(OrderStatus.NEW)
            .build();

    when(cartRepository.findById(3L)).thenReturn(Optional.of(cartEntity));
    when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrderEntity);
    when(orderMapper.toDomain(savedOrderEntity)).thenReturn(resultDomain);

    Order result = orderService.createOrderFromCart(3L);

    ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
    verify(orderRepository).save(captor.capture());
    OrderEntity captured = captor.getValue();

    assertThat(captured.getTotalPrice()).isEqualTo(15.0);
    assertThat(captured.getStatus()).isEqualTo(OrderStatus.NEW);
    assertThat(captured.getProducts()).containsExactly(p1, p2);

    verify(cartRepository).delete(cartEntity);

    assertThat(result.getId()).isEqualTo(10L);
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
    OrderEntity existing = new OrderEntity();
    existing.setId(1L);
    existing.setStatus(OrderStatus.NEW);
    OrderEntity saved = new OrderEntity();
    saved.setId(1L);
    saved.setStatus(OrderStatus.PROCESSING);

    Order resultDomain = Order.builder().id(1L).status(OrderStatus.PROCESSING).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(orderRepository.save(existing)).thenReturn(saved);
    when(orderMapper.toDomain(saved)).thenReturn(resultDomain);

    Order result = orderService.updateOrderStatus(1L, "Processing");

    ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
    verify(orderRepository).save(captor.capture());

    assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.PROCESSING);
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

  @Test
  @DisplayName("should get top selling products using projections")
  void getTopSellingProductsShouldCallRepository() {
    int limit = 5;
    ProductSalesStats statsMock = mock(ProductSalesStats.class);
    when(statsMock.getProductName()).thenReturn("Top Product");
    when(statsMock.getSalesCount()).thenReturn(100L);

    List<ProductSalesStats> expectedList = List.of(statsMock);

    when(orderRepository.getTopSellingProducts(any(PageRequest.class))).thenReturn(expectedList);

    List<ProductSalesStats> result = orderService.getTopSellingProducts(limit);

    verify(orderRepository).getTopSellingProducts(eq(PageRequest.of(0, limit)));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getProductName()).isEqualTo("Top Product");
  }
}
