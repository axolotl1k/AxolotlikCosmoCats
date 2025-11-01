package org.axolotlik.axolotlikcosmocats.service.exception;

public class OrderNotFoundException extends NotFoundException {
  public OrderNotFoundException(Long id) {
    super("Order", id);
  }
}
