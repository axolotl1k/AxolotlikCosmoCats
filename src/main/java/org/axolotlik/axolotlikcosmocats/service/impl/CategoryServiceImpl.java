package org.axolotlik.axolotlikcosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
    return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category", id));
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
      throw new NotFoundException("Category", id);
    }
    updatedCategory.setId(id);
    return categoryRepository.save(id, updatedCategory);
  }

  @Override
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }
}
