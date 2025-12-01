package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.service.exception.CategoryNotFoundException;
import org.axolotlik.axolotlikcosmocats.service.mapper.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CategoryServiceImpl.class})
@DisplayName("CategoryServiceImpl tests")
class CategoryServiceImplTest {

  @MockBean private CategoryRepository categoryRepository;
  @MockBean private CategoryMapper categoryMapper;

  @Autowired private CategoryServiceImpl categoryService;

  @Test
  @DisplayName("should return all categories")
  void getAllCategoriesShouldReturnList() {
    CategoryEntity e1 = new CategoryEntity(); e1.setId(1L); e1.setName("Clothes");
    CategoryEntity e2 = new CategoryEntity(); e2.setId(2L); e2.setName("Food");
    List<CategoryEntity> entities = List.of(e1, e2);

    Category c1 = new Category(1L, "Clothes", "Cosmic fashion");
    Category c2 = new Category(2L, "Food", "Space snacks");
    List<Category> domains = List.of(c1, c2);

    when(categoryRepository.findAll()).thenReturn(entities);
    when(categoryMapper.toDomainList(entities)).thenReturn(domains);

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
    CategoryEntity entity = new CategoryEntity(); entity.setId(1L); entity.setName("Toys");
    Category category = new Category(1L, "Toys", "For space cats");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(categoryMapper.toDomain(entity)).thenReturn(category);

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
  @DisplayName("should create category and return created domain")
  void createCategoryShouldSetIdAndSave() {
    Category input = new Category(null, "Cosmo Drinks", "Zero-gravity coffee");

    CategoryEntity mappedEntity = new CategoryEntity();
    mappedEntity.setName("Cosmo Drinks");

    CategoryEntity savedEntity = new CategoryEntity();
    savedEntity.setId(10L);
    savedEntity.setName("Cosmo Drinks");

    Category output = new Category(10L, "Cosmo Drinks", "Zero-gravity coffee");

    when(categoryMapper.toEntity(input)).thenReturn(mappedEntity);
    when(categoryRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(categoryMapper.toDomain(savedEntity)).thenReturn(output);

    Category result = categoryService.createCategory(input);

    ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(captor.capture());

    CategoryEntity captured = captor.getValue();
    assertThat(captured.getName()).isEqualTo("Cosmo Drinks");

    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getName()).isEqualTo("Cosmo Drinks");
    assertThat(result.getDescription()).isEqualTo("Zero-gravity coffee");
  }

  @Test
  @DisplayName("should update existing category successfully")
  void updateCategoryShouldSaveUpdatedEntity() {
    Long id = 5L;
    Category input = new Category(null, "Updated", "Edited");

    CategoryEntity mappedEntity = new CategoryEntity();
    mappedEntity.setId(id);
    mappedEntity.setName("Updated");

    CategoryEntity savedEntity = new CategoryEntity(); savedEntity.setId(id);
    Category output = new Category(id, "Updated", "Edited");

    when(categoryRepository.existsById(id)).thenReturn(true);
    when(categoryMapper.toEntity(input)).thenReturn(mappedEntity);
    when(categoryRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(categoryMapper.toDomain(savedEntity)).thenReturn(output);

    Category result = categoryService.updateCategory(id, input);

    verify(categoryRepository).existsById(id);

    ArgumentCaptor<CategoryEntity> captor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(captor.capture());

    CategoryEntity captured = captor.getValue();
    assertThat(captured.getId()).isEqualTo(id);
    assertThat(captured.getName()).isEqualTo("Updated");

    assertThat(result.getId()).isEqualTo(id);
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
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("should delete category by id")
  void deleteCategoryShouldCallRepository() {
    categoryService.deleteCategory(3L);

    verify(categoryRepository).deleteById(3L);
  }
}
