package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.impl.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.CartService;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;

  @Override
  public List<Cart> getAllCarts() {
    return cartRepository.findAll();
  }

  @Override
  public Cart getCartById(Long id) {
    return cartRepository.findById(id).orElseThrow(() -> new CartNotFoundException(id));
  }

  @Override
  public Cart createCart(List<Long> productIds) {
    List<Product> products = validateProductsExist(productIds);

    Cart cart = Cart.builder().products(products).build();

    Long id = cartRepository.generateId();
    cart.setId(id);

    return cartRepository.save(id, cart);
  }

  @Override
  public Cart updateCartContents(Long id, List<Long> addProductIds, List<Long> removeProductIds) {
    Cart cart = getCartById(id);
    Set<Product> current = new HashSet<>(cart.getProducts());

    if (addProductIds != null) {
      current.addAll(validateProductsExist(addProductIds));
    }
    if (removeProductIds != null) {
      current.removeIf(p -> removeProductIds.contains(p.getId()));
    }

    cart.setProducts(new ArrayList<>(current));
    return cartRepository.save(id, cart);
  }

  @Override
  public void deleteCart(Long id) {
    cartRepository.deleteById(id);
  }

  private List<Product> validateProductsExist(List<Long> ids) {
    List<Product> found =
        productRepository.findAll().stream().filter(p -> ids.contains(p.getId())).toList();

    if (found.size() != ids.size()) {
      throw new ProductNotFoundException("One or more products not found for IDs: " + ids);
    }
    return found;
  }
}
