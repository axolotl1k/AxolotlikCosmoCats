package org.axolotlik.axolotlikcosmocats.service;

import org.axolotlik.axolotlikcosmocats.domain.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(Long categoryId);

    Product getProductById(Long id);

    Product createProduct(Product product, Long categoryId);

    Product updateProduct(Long id, Product product, Long categoryId);

    void deleteProduct(Long id);
}
