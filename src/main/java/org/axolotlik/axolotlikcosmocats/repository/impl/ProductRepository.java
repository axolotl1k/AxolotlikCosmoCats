package org.axolotlik.axolotlikcosmocats.repository.impl;

import org.axolotlik.axolotlikcosmocats.domain.Product;
import org.axolotlik.axolotlikcosmocats.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends InMemoryRepository<Product> {
}
