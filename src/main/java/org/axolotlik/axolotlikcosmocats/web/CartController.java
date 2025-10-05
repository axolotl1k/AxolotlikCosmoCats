package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.cart.*;
import org.axolotlik.axolotlikcosmocats.service.CartService;
import org.axolotlik.axolotlikcosmocats.service.mapper.CartMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public CartListResponseDto getAll() {
        return cartMapper.toCartListResponseDto(cartService.getAllCarts());
    }

    @GetMapping("/{id}")
    public CartResponseDto getById(@PathVariable Long id) {
        return cartMapper.toCartResponseDto(cartService.getCartById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDto create(@RequestBody @Valid CartRequestDto dto) {
        return cartMapper.toCartResponseDto(cartService.createCart(dto.getProductIds()));
    }

    @PatchMapping("/{id}")
    public CartResponseDto update(@PathVariable Long id, @RequestBody @Valid CartUpdateRequestDto dto) {
        return cartMapper.toCartResponseDto(
                cartService.updateCartContents(id, dto.getAddProductIds(), dto.getRemoveProductIds()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cartService.deleteCart(id);
    }
}
