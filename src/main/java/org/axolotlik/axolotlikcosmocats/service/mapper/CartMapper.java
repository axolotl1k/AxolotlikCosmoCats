package org.axolotlik.axolotlikcosmocats.service.mapper;

import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.cart.CartResponseDto;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class})
public interface CartMapper {

  // === Domain <-> DTO ===
  @Mapping(target = "id", source = "id")
  @Mapping(target = "products", source = "products")
  @Mapping(target = "totalPrice", source = "totalPrice")
  CartResponseDto toCartResponseDto(Cart cart);

  List<CartResponseDto> toCartResponseDtoList(List<Cart> carts);

  default CartListResponseDto toCartListResponseDto(List<Cart> carts) {
    return CartListResponseDto.builder().carts(toCartResponseDtoList(carts)).build();
  }

  // === Domain <-> Entity (НОВЕ) ===
  Cart toDomain(CartEntity entity);

  CartEntity toEntity(Cart domain);

  List<Cart> toDomainList(List<CartEntity> entities);
}
