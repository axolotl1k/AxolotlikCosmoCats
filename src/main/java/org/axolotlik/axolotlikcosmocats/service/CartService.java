package org.axolotlik.axolotlikcosmocats.service;

import org.axolotlik.axolotlikcosmocats.domain.Cart;

import java.util.List;

public interface CartService {

    List<Cart> getAllCarts();

    Cart getCartById(Long id);

    Cart createCart(List<Long> productIds);

    Cart updateCartContents(Long id, List<Long> addProductIds, List<Long> removeProductIds);

    void deleteCart(Long id);
}
