package org.axolotlik.axolotlikcosmocats.service;

import org.axolotlik.axolotlikcosmocats.domain.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category updatedCategory);

    void deleteCategory(Long id);
}
