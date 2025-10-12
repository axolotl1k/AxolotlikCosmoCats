package org.axolotlik.axolotlikcosmocats.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
  NEW("New"),
  PROCESSING("Processing"),
  PAID("Paid"),
  CANCELLED("Cancelled");

  private final String displayName;

  public static OrderStatus fromDisplayName(String displayName) {
    for (OrderStatus status : values()) {
      if (status.displayName.equalsIgnoreCase(displayName)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid order status: " + displayName);
  }
}
