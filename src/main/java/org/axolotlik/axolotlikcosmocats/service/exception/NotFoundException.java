package org.axolotlik.axolotlikcosmocats.service.exception;

public abstract class NotFoundException extends RuntimeException {
  public static final String ID_NOT_FOUND = "%s with id %s not found";

  public NotFoundException(String objectType, Long id) {
    super(String.format(ID_NOT_FOUND, objectType, id));
  }

  public NotFoundException(String message) {
    super(message);
  }
}
