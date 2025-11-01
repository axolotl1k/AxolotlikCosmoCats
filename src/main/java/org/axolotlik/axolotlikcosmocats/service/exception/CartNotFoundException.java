package org.axolotlik.axolotlikcosmocats.service.exception;

public class CartNotFoundException extends NotFoundException {
  public CartNotFoundException(Long id) {
    super("Cart", id);
  }
}
