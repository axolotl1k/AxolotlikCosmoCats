package org.axolotlik.axolotlikcosmocats.service.exception;

public class CategoryNotFoundException extends NotFoundException {
  public CategoryNotFoundException(Long id) {
    super("Category", id);
  }
}
