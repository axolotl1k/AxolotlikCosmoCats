package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderStatusUpdateRequestDto;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.OrderRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.OrderEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
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
@DisplayName("OrderController integration tests (full field coverage)")
@WithMockUser(roles = "ADMIN")
class OrderControllerIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private OrderRepository orderRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;

  @SpyBean private OrderService orderService;

  @BeforeEach
  void setUp() {
    reset(orderService);
    orderRepository.deleteAll();
    cartRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all orders (200 OK)")
  @WithMockUser(roles = "ADMIN")
  void shouldGetAllOrders() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Food", "Cosmic meals", new ArrayList<>()));
    var p1 =
        productRepository.save(
            new ProductEntity(null, "Space Burger", "Zero-G delight", 12.0, true, cat));
    var p2 =
        productRepository.save(
            new ProductEntity(null, "Comet Coffee", "Strong brew", 6.0, true, cat));

    orderRepository.save(new OrderEntity(null, 18.0, OrderStatus.NEW, List.of(p1, p2)));
    orderRepository.save(new OrderEntity(null, 6.0, OrderStatus.NEW, List.of(p2)));

    mockMvc
        .perform(get("/api/v1/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orders").isArray())
        .andExpect(jsonPath("$.orders.length()").value(2))

        .andExpect(jsonPath("$.orders[0].totalPrice").value(18.0))
        .andExpect(jsonPath("$.orders[0].status").value("NEW"))
        .andExpect(jsonPath("$.orders[0].products.length()").value(2))

        .andExpect(jsonPath("$.orders[0].products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.orders[0].products[0].name").value("Space Burger"))
        .andExpect(jsonPath("$.orders[0].products[0].description").value("Zero-G delight"))
        .andExpect(jsonPath("$.orders[0].products[0].price").value(12.0))
        .andExpect(jsonPath("$.orders[0].products[0].available").value(true))
        .andExpect(jsonPath("$.orders[0].products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.orders[0].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.orders[0].products[0].category.description").value("Cosmic meals"))

        .andExpect(jsonPath("$.orders[0].products[1].id").value(p2.getId()))
        .andExpect(jsonPath("$.orders[0].products[1].name").value("Comet Coffee"))
        .andExpect(jsonPath("$.orders[0].products[1].description").value("Strong brew"))
        .andExpect(jsonPath("$.orders[0].products[1].price").value(6.0))
        .andExpect(jsonPath("$.orders[0].products[1].available").value(true))
        .andExpect(jsonPath("$.orders[0].products[1].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.orders[0].products[1].category.name").value("Food"))
        .andExpect(jsonPath("$.orders[0].products[1].category.description").value("Cosmic meals"))

        .andExpect(jsonPath("$.orders[1].totalPrice").value(6.0))
        .andExpect(jsonPath("$.orders[1].status").value("NEW"))
        .andExpect(jsonPath("$.orders[1].products.length()").value(1))

        .andExpect(jsonPath("$.orders[1].products[0].id").value(p2.getId()))
        .andExpect(jsonPath("$.orders[1].products[0].name").value("Comet Coffee"))
        .andExpect(jsonPath("$.orders[1].products[0].description").value("Strong brew"))
        .andExpect(jsonPath("$.orders[1].products[0].price").value(6.0))
        .andExpect(jsonPath("$.orders[1].products[0].available").value(true))
        .andExpect(jsonPath("$.orders[1].products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.orders[1].products[0].category.name").value("Food"))
        .andExpect(jsonPath("$.orders[1].products[0].category.description").value("Cosmic meals"));

    verify(orderService).getAllOrders();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get order by id (200 OK)")
  void shouldGetOrderById() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Drinks", "Liquid refreshments", new ArrayList<>()));
    var p1 =
        productRepository.save(new ProductEntity(null, "Galaxy Soda", "Fizzy", 4.0, true, cat));
    var order = orderRepository.save(new OrderEntity(null, 4.0, OrderStatus.NEW, List.of(p1)));

    mockMvc
        .perform(get("/api/v1/orders/{id}", order.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(order.getId()))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.totalPrice").value(4.0))
        .andExpect(jsonPath("$.products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Galaxy Soda"))
        .andExpect(jsonPath("$.products[0].description").value("Fizzy"))
        .andExpect(jsonPath("$.products[0].price").value(4.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Drinks"))
        .andExpect(jsonPath("$.products[0].category.description").value("Liquid refreshments"));

    verify(orderService).getOrderById(order.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when order not found")
  void shouldReturnNotFoundForMissingOrder() {
    long id = 404L;
    mockMvc
        .perform(get("/api/v1/orders/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Order", id)));

    verify(orderService).getOrderById(id);
  }

  @Test
  @SneakyThrows
  @DisplayName("should create order from existing cart (201 Created)")
  void shouldCreateOrderFromCart() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Snacks", "Space food", new ArrayList<>()));
    var p1 =
        productRepository.save(new ProductEntity(null, "Star Donut", "Sweet ring", 3.5, true, cat));
    var p2 =
        productRepository.save(
            new ProductEntity(null, "Nebula Bar", "Energy snack", 4.5, true, cat));
    var cart = cartRepository.save(new CartEntity(null, List.of(p1, p2)));
    OrderRequestDto request = OrderRequestDto.builder().cartId(cart.getId()).build();

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.totalPrice").value(8.0))
        .andExpect(jsonPath("$.products.length()").value(2))

        .andExpect(jsonPath("$.products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Star Donut"))
        .andExpect(jsonPath("$.products[0].description").value("Sweet ring"))
        .andExpect(jsonPath("$.products[0].price").value(3.5))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Snacks"))
        .andExpect(jsonPath("$.products[0].category.description").value("Space food"))

        .andExpect(jsonPath("$.products[1].id").value(p2.getId()))
        .andExpect(jsonPath("$.products[1].name").value("Nebula Bar"))
        .andExpect(jsonPath("$.products[1].description").value("Energy snack"))
        .andExpect(jsonPath("$.products[1].price").value(4.5))
        .andExpect(jsonPath("$.products[1].available").value(true))
        .andExpect(jsonPath("$.products[1].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[1].category.name").value("Snacks"))
        .andExpect(jsonPath("$.products[1].category.description").value("Space food"));

    verify(orderService).createOrderFromCart(cart.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when creating order from missing cart")
  void shouldReturnNotFoundWhenCartMissing() {
    OrderRequestDto req = OrderRequestDto.builder().cartId(222L).build();

    mockMvc
        .perform(
            post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Cart", 222L)));

    verify(orderService).createOrderFromCart(222L);
  }

  @Test
  @SneakyThrows
  @DisplayName("should update order status (200 OK)")
  void shouldUpdateOrderStatus() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "Electronics", "Tech stuff", new ArrayList<>()));
    var p1 =
        productRepository.save(
            new ProductEntity(null, "Quantum Phone", "Super fast", 1000.0, true, cat));
    var order = orderRepository.save(new OrderEntity(null, 1000.0, OrderStatus.NEW, List.of(p1)));

    OrderStatusUpdateRequestDto update =
        OrderStatusUpdateRequestDto.builder().status("Processing").build();

    mockMvc
        .perform(
            patch("/api/v1/orders/{id}", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(order.getId()))
        .andExpect(jsonPath("$.status").value("PROCESSING"))
        .andExpect(jsonPath("$.totalPrice").value(1000.0))
        .andExpect(jsonPath("$.products[0].id").value(p1.getId()))
        .andExpect(jsonPath("$.products[0].name").value("Quantum Phone"))
        .andExpect(jsonPath("$.products[0].description").value("Super fast"))
        .andExpect(jsonPath("$.products[0].price").value(1000.0))
        .andExpect(jsonPath("$.products[0].available").value(true))
        .andExpect(jsonPath("$.products[0].category.id").value(cat.getId()))
        .andExpect(jsonPath("$.products[0].category.name").value("Electronics"))
        .andExpect(jsonPath("$.products[0].category.description").value("Tech stuff"));

    verify(orderService).updateOrderStatus(eq(order.getId()), any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 400 for invalid status update request")
  void shouldReturnBadRequestForInvalidStatusUpdate() {
    var order =
        orderRepository.save(new OrderEntity(null, 0.0, OrderStatus.NEW, new ArrayList<>()));

    OrderStatusUpdateRequestDto invalid = OrderStatusUpdateRequestDto.builder().status("").build();

    mockMvc
        .perform(
            patch("/api/v1/orders/{id}", order.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors[0].field").value("status"))
        .andExpect(jsonPath("$.errors[0].reason").value("must not be blank"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete order (204 No Content)")
  void shouldDeleteOrder() {
    var order =
        orderRepository.save(new OrderEntity(null, 15.0, OrderStatus.NEW, new ArrayList<>()));

    mockMvc.perform(delete("/api/v1/orders/{id}", order.getId())).andExpect(status().isNoContent());

    verify(orderService).deleteOrder(order.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should get top selling products report (200 OK)")
  void shouldGetTopSellingReport() {
    var cat =
        categoryRepository.save(
            new CategoryEntity(null, "ReportCat", "For report", new ArrayList<>()));
    var p1 =
        productRepository.save(new ProductEntity(null, "Popular Item", "Desc", 10.0, true, cat));
    var p2 = productRepository.save(new ProductEntity(null, "Rare Item", "Desc", 20.0, true, cat));

    orderRepository.save(new OrderEntity(null, 10.0, OrderStatus.PAID, List.of(p1)));
    orderRepository.save(new OrderEntity(null, 10.0, OrderStatus.PAID, List.of(p1)));
    orderRepository.save(new OrderEntity(null, 10.0, OrderStatus.PAID, List.of(p1)));

    orderRepository.save(new OrderEntity(null, 20.0, OrderStatus.PAID, List.of(p2)));

    mockMvc
        .perform(get("/api/v1/orders/stats/top?limit=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].productName").value("Popular Item"))
        .andExpect(jsonPath("$[0].salesCount").value(3))
        .andExpect(jsonPath("$[1].productName").value("Rare Item"))
        .andExpect(jsonPath("$[1].salesCount").value(1));

    verify(orderService).getTopSellingProducts(5);
  }

  @Test
  @DisplayName("should return 403 Forbidden when USER tries to get ALL orders")
  @WithMockUser(roles = "USER")
  @SneakyThrows
  void shouldReturnForbiddenForUserGetAllOrders() {
    mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("should return 403 Forbidden when USER tries to get stats")
  @WithMockUser(roles = "USER")
  @SneakyThrows
  void shouldReturnForbiddenForUserGetStats() {
    mockMvc.perform(get("/api/v1/orders/stats/top"))
            .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("should return 403 Forbidden when USER tries to update status")
  @WithMockUser(roles = "USER")
  @SneakyThrows
  void shouldReturnForbiddenForUserUpdateStatus() {
    OrderStatusUpdateRequestDto request = OrderStatusUpdateRequestDto.builder().status("SHIPPED").build();

    mockMvc.perform(patch("/api/v1/orders/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
  }
}
