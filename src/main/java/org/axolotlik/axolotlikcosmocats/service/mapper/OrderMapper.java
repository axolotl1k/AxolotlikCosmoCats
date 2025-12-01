package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.order.OrderResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.order.ProductSalesStatsDto;
import org.axolotlik.axolotlikcosmocats.repository.entity.OrderEntity;
import org.axolotlik.axolotlikcosmocats.repository.projection.ProductSalesStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class})
public interface OrderMapper {

  // === Domain <-> DTO ===
  @Mapping(target = "id", source = "id")
  @Mapping(target = "products", source = "products")
  @Mapping(target = "totalPrice", source = "totalPrice")
  @Mapping(target = "status", source = "status")
  OrderResponseDto toOrderResponseDto(Order order);

  List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders);

  default OrderListResponseDto toOrderListResponseDto(List<Order> orders) {
    return OrderListResponseDto.builder().orders(toOrderResponseDtoList(orders)).build();
  }

  ProductSalesStatsDto toSalesStatsDto(ProductSalesStats stats);

  List<ProductSalesStatsDto> toSalesStatsDtoList(List<ProductSalesStats> statsList);

  // === Domain <-> Entity (НОВЕ) ===
  Order toDomain(OrderEntity entity);

  OrderEntity toEntity(Order domain);

  List<Order> toDomainList(List<OrderEntity> entities);
}
