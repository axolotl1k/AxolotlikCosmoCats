package org.axolotlik.axolotlikcosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Cart {
    private Long id;
    private List<Product> products;

    public Double getTotalPrice() {
        if (products == null || products.isEmpty()) return 0.0;
        return products.stream().mapToDouble(Product::getPrice).sum();
    }
}
