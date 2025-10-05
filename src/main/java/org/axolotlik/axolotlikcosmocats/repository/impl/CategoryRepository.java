package org.axolotlik.axolotlikcosmocats.repository.impl;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository extends InMemoryRepository<Category> {
}
