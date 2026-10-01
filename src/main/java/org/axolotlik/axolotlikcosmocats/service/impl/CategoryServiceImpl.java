package org.axolotlik.axolotlikcosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Override
  public List<Category> getAllCategories() {
    return categoryMapper.toDomainList(categoryRepository.findAll());
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository
        .findById(id)
        .map(categoryMapper::toDomain)
        .orElseThrow(() -> new CategoryNotFoundException(id));
  }

  @Override
  @Transactional
  public Category createCategory(Category category) {
    CategoryEntity entity = categoryMapper.toEntity(category);
    return categoryMapper.toDomain(categoryRepository.save(entity));
  }

  @Override
  @Transactional
  public Category updateCategory(Long id, Category updatedCategory) {
    if (!categoryRepository.existsById(id)) {
      throw new CategoryNotFoundException(id);
    }
    updatedCategory.setId(id);
    CategoryEntity entity = categoryMapper.toEntity(updatedCategory);
    return categoryMapper.toDomain(categoryRepository.save(entity));
  }

  @Override
  @Transactional
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }
}
