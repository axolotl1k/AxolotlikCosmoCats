package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;

@SpringBootTest(classes = {CategoryServiceImpl.class})
@DisplayName("CategoryServiceImpl tests")
class CategoryServiceImplTest {

  @MockBean
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryServiceImpl categoryService;

  @Test
  @DisplayName("should return all categories")
  void getAllCategoriesShouldReturnList() {
    List<Category> categories = List.of(
            new Category(1L, "Clothes", "Cosmic fashion"),
            new Category(2L, "Food", "Space snacks")
    );
    when(categoryRepository.findAll()).thenReturn(categories);

    List<Category> result = categoryService.getAllCategories();

    verify(categoryRepository).findAll();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    assertThat(result.get(0).getName()).isEqualTo("Clothes");
    assertThat(result.get(0).getDescription()).isEqualTo("Cosmic fashion");
    assertThat(result.get(1).getId()).isEqualTo(2L);
    assertThat(result.get(1).getName()).isEqualTo("Food");
    assertThat(result.get(1).getDescription()).isEqualTo("Space snacks");
  }

  @Test
  @DisplayName("should return category by id when exists")
  void getCategoryByIdShouldReturnCategory() {
    Category category = new Category(1L, "Toys", "For space cats");
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    Category result = categoryService.getCategoryById(1L);

    verify(categoryRepository).findById(1L);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("Toys");
    assertThat(result.getDescription()).isEqualTo("For space cats");
  }

  @Test
  @DisplayName("should throw CategoryNotFoundException when category not found")
  void getCategoryByIdShouldThrowIfNotFound() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.getCategoryById(99L))
            .isInstanceOf(CategoryNotFoundException.class)
            .hasMessage(String.format(ID_NOT_FOUND, "Category", 99L));

    verify(categoryRepository).findById(99L);
  }

  @Test
  @DisplayName("should create category and assign generated id")
  void createCategoryShouldSetIdAndSave() {
    Category category = new Category(null, "Cosmo Drinks", "Zero-gravity coffee");
    when(categoryRepository.generateId()).thenReturn(10L);
    when(categoryRepository.save(10L, category)).thenReturn(category);

    Category result = categoryService.createCategory(category);

    verify(categoryRepository).generateId();
    verify(categoryRepository).save(10L, category);

    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getName()).isEqualTo("Cosmo Drinks");
    assertThat(result.getDescription()).isEqualTo("Zero-gravity coffee");
  }

  @Test
  @DisplayName("should update existing category successfully")
  void updateCategoryShouldSaveUpdatedEntity() {
    Category updated = new Category(null, "Updated", "Edited");
    when(categoryRepository.existsById(5L)).thenReturn(true);
    when(categoryRepository.save(5L, updated)).thenReturn(updated);

    Category result = categoryService.updateCategory(5L, updated);

    verify(categoryRepository).existsById(5L);
    verify(categoryRepository).save(5L, updated);

    assertThat(result.getId()).isEqualTo(5L);
    assertThat(result.getName()).isEqualTo("Updated");
    assertThat(result.getDescription()).isEqualTo("Edited");
  }

  @Test
  @DisplayName("should throw CategoryNotFoundException when updating non-existing category")
  void updateCategoryShouldThrowIfNotExists() {
    when(categoryRepository.existsById(999L)).thenReturn(false);

    assertThatThrownBy(() -> categoryService.updateCategory(999L, Category.builder().build()))
            .isInstanceOf(CategoryNotFoundException.class)
            .hasMessage(String.format(ID_NOT_FOUND, "Category", 999L));

    verify(categoryRepository).existsById(999L);
  }

  @Test
  @DisplayName("should delete category by id")
  void deleteCategoryShouldCallRepository() {
    categoryService.deleteCategory(3L);

    verify(categoryRepository).deleteById(3L);
  }
}
