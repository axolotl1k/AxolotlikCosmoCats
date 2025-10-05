package org.axolotlik.axolotlikcosmocats.repository.impl;

import org.axolotlik.axolotlikcosmocats.domain.Order;
import org.axolotlik.axolotlikcosmocats.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository extends InMemoryRepository<Order> {
}
