package org.axolotlik.axolotlikcosmocats.service.exception;

public class ProductNotFoundException extends NotFoundException {
  public ProductNotFoundException(Long id) {
    super("Product", id);
  }

  public ProductNotFoundException(String message) {
    super(message);
  }
}
