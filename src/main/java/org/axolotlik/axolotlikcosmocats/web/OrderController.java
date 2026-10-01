package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.order.*;
import org.axolotlik.axolotlikcosmocats.repository.projection.ProductSalesStats;
import org.axolotlik.axolotlikcosmocats.service.OrderService;
import org.axolotlik.axolotlikcosmocats.service.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final OrderMapper orderMapper;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public OrderListResponseDto getAll() {
    return orderMapper.toOrderListResponseDto(orderService.getAllOrders());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public OrderResponseDto getById(@PathVariable Long id) {
    return orderMapper.toOrderResponseDto(orderService.getOrderById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public OrderResponseDto create(@RequestBody @Valid OrderRequestDto dto) {
    return orderMapper.toOrderResponseDto(orderService.createOrderFromCart(dto.getCartId()));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public OrderResponseDto updateStatus(
      @PathVariable Long id, @RequestBody @Valid OrderStatusUpdateRequestDto dto) {
    return orderMapper.toOrderResponseDto(orderService.updateOrderStatus(id, dto.getStatus()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    orderService.deleteOrder(id);
  }

  @GetMapping("/stats/top")
  @PreAuthorize("hasRole('ADMIN')")
  public List<ProductSalesStatsDto> getTopSellingReport(
      @RequestParam(defaultValue = "5") int limit) {
    return orderMapper.toSalesStatsDtoList(orderService.getTopSellingProducts(limit));
  }
}
