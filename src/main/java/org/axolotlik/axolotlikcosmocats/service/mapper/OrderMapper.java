package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class})
public interface OrderMapper {
  @Mapping(target = "id", source = "id")
  @Mapping(target = "products", source = "products")
  @Mapping(target = "totalPrice", source = "totalPrice")
  @Mapping(target = "status", source = "status")
  OrderResponseDto toOrderResponseDto(Order order);

  List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders);

  default OrderListResponseDto toOrderListResponseDto(List<Order> orders) {
    return OrderListResponseDto.builder().orders(toOrderResponseDtoList(orders)).build();
  }
}
