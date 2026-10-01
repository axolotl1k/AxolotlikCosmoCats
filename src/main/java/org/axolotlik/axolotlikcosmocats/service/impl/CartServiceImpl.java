package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.repository.CartRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.CartService;
import org.axolotlik.axolotlikcosmocats.service.exception.CartNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.exception.ProductNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final CartMapper cartMapper;

  @Override
  public List<Cart> getAllCarts() {
    return cartMapper.toDomainList(cartRepository.findAll());
  }

  @Override
  public Cart getCartById(Long id) {
    return cartRepository
        .findById(id)
        .map(cartMapper::toDomain)
        .orElseThrow(() -> new CartNotFoundException(id));
  }

  @Override
  @Transactional
  public Cart createCart(List<Long> productIds) {
    List<ProductEntity> productEntities = validateProductsExist(productIds);

    CartEntity cartEntity = new CartEntity();
    cartEntity.setProducts(productEntities);

    return cartMapper.toDomain(cartRepository.save(cartEntity));
  }

  @Override
  @Transactional
  public Cart updateCartContents(Long id, List<Long> addProductIds, List<Long> removeProductIds) {
    CartEntity cartEntity =
        cartRepository.findById(id).orElseThrow(() -> new CartNotFoundException(id));

    Set<ProductEntity> currentProducts = new LinkedHashSet<>(cartEntity.getProducts());

    if (addProductIds != null && !addProductIds.isEmpty()) {
      currentProducts.addAll(validateProductsExist(addProductIds));
    }
    if (removeProductIds != null && !removeProductIds.isEmpty()) {
      currentProducts.removeIf(p -> removeProductIds.contains(p.getId()));
    }

    cartEntity.setProducts(new ArrayList<>(currentProducts));
    return cartMapper.toDomain(cartRepository.save(cartEntity));
  }

  @Override
  @Transactional
  public void deleteCart(Long id) {
    cartRepository.deleteById(id);
  }

  private List<ProductEntity> validateProductsExist(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return new ArrayList<>();

    Set<Long> uniqueIds = new LinkedHashSet<>(ids);

    List<ProductEntity> found = productRepository.findAllById(uniqueIds);

    if (found.size() != uniqueIds.size()) {
      Set<Long> foundIds = found.stream().map(ProductEntity::getId).collect(Collectors.toSet());
      uniqueIds.removeAll(foundIds);
      throw new ProductNotFoundException("Products not found for IDs: " + uniqueIds);
    }
    return found;
  }
}
