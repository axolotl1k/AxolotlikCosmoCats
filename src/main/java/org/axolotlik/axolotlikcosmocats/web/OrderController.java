package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.order.*;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
import org.axolotlik.axolotlikcosmocats.service.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public OrderListResponseDto getAll() {
        return orderMapper.toOrderListResponseDto(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public OrderResponseDto getById(@PathVariable Long id) {
        return orderMapper.toOrderResponseDto(orderService.getOrderById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@RequestBody @Valid OrderRequestDto dto) {
        return orderMapper.toOrderResponseDto(orderService.createOrderFromCart(dto.getCartId()));
    }

    @PatchMapping("/{id}")
    public OrderResponseDto updateStatus(@PathVariable Long id, @RequestBody @Valid OrderStatusUpdateRequestDto dto) {
        return orderMapper.toOrderResponseDto(orderService.updateOrderStatus(id, dto.getStatus()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
