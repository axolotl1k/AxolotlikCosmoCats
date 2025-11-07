package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.repository.impl.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.OrderRepository;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;

  @Override
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
  }

  @Override
  public Order createOrderFromCart(Long cartId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));

    Order order =
        Order.builder()
            .products(cart.getProducts())
            .status(OrderStatus.NEW)
            .totalPrice(cart.getTotalPrice())
            .build();

    Long id = orderRepository.generateId();
    order.setId(id);
    cartRepository.deleteById(cartId);
    return orderRepository.save(id, order);
  }

  @Override
  public Order updateOrderStatus(Long id, String newStatus) {
    Order order = getOrderById(id);
    order.setStatus(OrderStatus.fromDisplayName(newStatus));
    return orderRepository.save(id, order);
  }

  @Override
  public void deleteOrder(Long id) {
    orderRepository.deleteById(id);
  }
}
