package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;
import org.axolotlik.axolotlikcosmocats.domain.*;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderStatusUpdateRequestDto;
import org.axolotlik.axolotlikcosmocats.repository.impl.*;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
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
@DisplayName("OrderController integration tests (full field coverage)")
class OrderControllerIT {

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
        orderRepository.clear();
        cartRepository.clear();
        productRepository.clear();
        categoryRepository.clear();
    }

    @Test
    @SneakyThrows
    @DisplayName("should get all orders (200 OK)")
    void shouldGetAllOrders() {
        Category cat = new Category(1L, "Food", "Cosmic meals");
        categoryRepository.save(1L, cat);
        Product p1 = new Product(1L, "Space Burger", "Zero-G delight", 12.0, cat, true);
        Product p2 = new Product(2L, "Comet Coffee", "Strong brew", 6.0, cat, true);
        productRepository.save(1L, p1);
        productRepository.save(2L, p2);
        orderRepository.save(1L, new Order(1L, List.of(p1, p2), 18.0, OrderStatus.NEW));
        orderRepository.save(2L, new Order(2L, List.of(p2), 6.0, OrderStatus.NEW));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.orders.length()").value(2))

                .andExpect(jsonPath("$.orders[0].id").value(1))
                .andExpect(jsonPath("$.orders[0].totalPrice").value(18.0))
                .andExpect(jsonPath("$.orders[0].status").value("NEW"))
                .andExpect(jsonPath("$.orders[0].products[0].id").value(1))
                .andExpect(jsonPath("$.orders[0].products[0].name").value("Space Burger"))
                .andExpect(jsonPath("$.orders[0].products[0].description").value("Zero-G delight"))
                .andExpect(jsonPath("$.orders[0].products[0].price").value(12.0))
                .andExpect(jsonPath("$.orders[0].products[0].available").value(true))
                .andExpect(jsonPath("$.orders[0].products[0].category.id").value(1))
                .andExpect(jsonPath("$.orders[0].products[0].category.name").value("Food"))
                .andExpect(jsonPath("$.orders[0].products[0].category.description").value("Cosmic meals"))

                .andExpect(jsonPath("$.orders[1].id").value(2))
                .andExpect(jsonPath("$.orders[1].totalPrice").value(6.0))
                .andExpect(jsonPath("$.orders[1].status").value("NEW"))
                .andExpect(jsonPath("$.orders[1].products[0].id").value(2))
                .andExpect(jsonPath("$.orders[1].products[0].name").value("Comet Coffee"))
                .andExpect(jsonPath("$.orders[1].products[0].description").value("Strong brew"))
                .andExpect(jsonPath("$.orders[1].products[0].price").value(6.0))
                .andExpect(jsonPath("$.orders[1].products[0].available").value(true))
                .andExpect(jsonPath("$.orders[1].products[0].category.id").value(1))
                .andExpect(jsonPath("$.orders[1].products[0].category.name").value("Food"))
                .andExpect(jsonPath("$.orders[1].products[0].category.description").value("Cosmic meals"));
    }

    @Test
    @SneakyThrows
    @DisplayName("should get order by id (200 OK)")
    void shouldGetOrderById() {
        Category cat = new Category(1L, "Drinks", "Liquid refreshments");
        categoryRepository.save(1L, cat);
        Product p1 = new Product(1L, "Galaxy Soda", "Fizzy", 4.0, cat, true);
        productRepository.save(1L, p1);
        orderRepository.save(1L, new Order(1L, List.of(p1), 4.0, OrderStatus.NEW));

        mockMvc.perform(get("/api/v1/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(4.0))
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Galaxy Soda"))
                .andExpect(jsonPath("$.products[0].description").value("Fizzy"))
                .andExpect(jsonPath("$.products[0].price").value(4.0))
                .andExpect(jsonPath("$.products[0].available").value(true))
                .andExpect(jsonPath("$.products[0].category.id").value(1))
                .andExpect(jsonPath("$.products[0].category.name").value("Drinks"))
                .andExpect(jsonPath("$.products[0].category.description").value("Liquid refreshments"));
    }

    @Test
    @SneakyThrows
    @DisplayName("should return 404 when order not found")
    void shouldReturnNotFoundForMissingOrder() {
        long id = 404L;
        mockMvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.type").value("not-found"))
                .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Order", id)));
    }

    @Test
    @SneakyThrows
    @DisplayName("should create order from existing cart (201 Created)")
    void shouldCreateOrderFromCart() {
        Category cat = new Category(1L, "Snacks", "Space food");
        categoryRepository.save(1L, cat);
        Product p1 = new Product(1L, "Star Donut", "Sweet ring", 3.5, cat, true);
        Product p2 = new Product(2L, "Nebula Bar", "Energy snack", 4.5, cat, true);
        productRepository.save(1L, p1);
        productRepository.save(2L, p2);
        Cart cart = new Cart(1L, List.of(p1, p2));
        cartRepository.save(1L, cart);

        OrderRequestDto request = OrderRequestDto.builder().cartId(1L).build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(8.0))
                .andExpect(jsonPath("$.products.length()").value(2))

                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Star Donut"))
                .andExpect(jsonPath("$.products[0].description").value("Sweet ring"))
                .andExpect(jsonPath("$.products[0].price").value(3.5))
                .andExpect(jsonPath("$.products[0].available").value(true))
                .andExpect(jsonPath("$.products[0].category.id").value(1))
                .andExpect(jsonPath("$.products[0].category.name").value("Snacks"))
                .andExpect(jsonPath("$.products[0].category.description").value("Space food"))

                .andExpect(jsonPath("$.products[1].id").value(2))
                .andExpect(jsonPath("$.products[1].name").value("Nebula Bar"))
                .andExpect(jsonPath("$.products[1].description").value("Energy snack"))
                .andExpect(jsonPath("$.products[1].price").value(4.5))
                .andExpect(jsonPath("$.products[1].available").value(true))
                .andExpect(jsonPath("$.products[1].category.id").value(1))
                .andExpect(jsonPath("$.products[1].category.name").value("Snacks"))
                .andExpect(jsonPath("$.products[1].category.description").value("Space food"));
    }

    @Test
    @SneakyThrows
    @DisplayName("should return 404 when creating order from missing cart")
    void shouldReturnNotFoundWhenCartMissing() {
        OrderRequestDto req = OrderRequestDto.builder().cartId(222L).build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.type").value("not-found"))
                .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Cart", 222L)));
    }

    @Test
    @SneakyThrows
    @DisplayName("should update order status (200 OK)")
    void shouldUpdateOrderStatus() {
        Category cat = new Category(1L, "Electronics", "Tech stuff");
        categoryRepository.save(1L, cat);
        Product p1 = new Product(1L, "Quantum Phone", "Super fast", 1000.0, cat, true);
        productRepository.save(1L, p1);
        Order order = new Order(1L, List.of(p1), 1000.0, OrderStatus.NEW);
        orderRepository.save(1L, order);

        OrderStatusUpdateRequestDto update = OrderStatusUpdateRequestDto.builder().status("Processing").build();

        mockMvc.perform(patch("/api/v1/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.totalPrice").value(1000.0))
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Quantum Phone"))
                .andExpect(jsonPath("$.products[0].description").value("Super fast"))
                .andExpect(jsonPath("$.products[0].price").value(1000.0))
                .andExpect(jsonPath("$.products[0].available").value(true))
                .andExpect(jsonPath("$.products[0].category.id").value(1))
                .andExpect(jsonPath("$.products[0].category.name").value("Electronics"))
                .andExpect(jsonPath("$.products[0].category.description").value("Tech stuff"));
    }

    @Test
    @SneakyThrows
    @DisplayName("should return 400 for invalid status update request")
    void shouldReturnBadRequestForInvalidStatusUpdate() {
        OrderStatusUpdateRequestDto invalid = OrderStatusUpdateRequestDto.builder().status("").build();

        mockMvc.perform(patch("/api/v1/orders/{id}", 1L)
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
        Category cat = new Category(1L, "Hardware", "Tools");
        categoryRepository.save(1L, cat);
        Product p1 = new Product(1L, "Wrench", "Heavy tool", 15.0, cat, true);
        productRepository.save(1L, p1);
        Order order = new Order(1L, List.of(p1), 15.0, OrderStatus.NEW);
        orderRepository.save(1L, order);

        mockMvc.perform(delete("/api/v1/orders/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
