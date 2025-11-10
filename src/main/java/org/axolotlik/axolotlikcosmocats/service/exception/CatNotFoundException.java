package org.axolotlik.axolotlikcosmocats.service.exception;

public class CatNotFoundException extends NotFoundException {
  public CatNotFoundException(String name) {
    super("Cat", name);
  }
}
