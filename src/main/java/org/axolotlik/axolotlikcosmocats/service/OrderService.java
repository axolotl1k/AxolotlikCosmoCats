package org.axolotlik.axolotlikcosmocats.service;

import org.axolotlik.axolotlikcosmocats.domain.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createOrderFromCart(Long cartId);

    Order updateOrderStatus(Long id, String newStatus);

    void deleteOrder(Long id);
}
