package org.axolotlik.axolotlikcosmocats.repository.impl;

import org.axolotlik.axolotlikcosmocats.domain.Cart;
import org.axolotlik.axolotlikcosmocats.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepository extends InMemoryRepository<Cart> {
}
