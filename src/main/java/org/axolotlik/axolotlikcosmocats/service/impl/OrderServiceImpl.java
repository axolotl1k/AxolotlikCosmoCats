package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.OrderRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.OrderEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.repository.projection.ProductSalesStats;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.OrderNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.OrderMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final OrderMapper orderMapper;

  @Override
  public List<Order> getAllOrders() {
    return orderMapper.toDomainList(orderRepository.findAll());
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository
        .findById(id)
        .map(orderMapper::toDomain)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }

  @Override
  @Transactional
  public Order createOrderFromCart(Long cartId) {
    CartEntity cart =
        cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));

    OrderEntity order = new OrderEntity();

    order.setProducts(new ArrayList<>(cart.getProducts()));
    order.setStatus(OrderStatus.NEW);

    double total = cart.getProducts().stream().mapToDouble(ProductEntity::getPrice).sum();
    order.setTotalPrice(total);

    OrderEntity savedOrder = orderRepository.save(order);
    cartRepository.delete(cart);
    return orderMapper.toDomain(savedOrder);
  }

  @Override
  @Transactional
  public Order updateOrderStatus(Long id, String newStatus) {
    OrderEntity order =
        orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

    order.setStatus(OrderStatus.fromDisplayName(newStatus));
    return orderMapper.toDomain(orderRepository.save(order));
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    orderRepository.deleteById(id);
  }

  @Override
  public List<ProductSalesStats> getTopSellingProducts(int limit) {
    return orderRepository.getTopSellingProducts(PageRequest.of(0, limit));
  }
}
