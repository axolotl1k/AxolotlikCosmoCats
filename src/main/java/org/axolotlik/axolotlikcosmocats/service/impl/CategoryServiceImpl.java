package org.axolotlik.axolotlikcosmocats.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new CategoryNotFoundException(id));
  }

  @Override
  public Category createCategory(Category category) {
    Long id = categoryRepository.generateId();
    category.setId(id);
    return categoryRepository.save(id, category);
  }

  @Override
  public Category updateCategory(Long id, Category updatedCategory) {
    if (!categoryRepository.existsById(id)) {
      throw new CategoryNotFoundException(id);
    }
    updatedCategory.setId(id);
    return categoryRepository.save(id, updatedCategory);
  }

  @Override
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }
}
