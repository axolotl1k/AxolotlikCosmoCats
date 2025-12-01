package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.AbstractIT;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryRequestDto;
import org.axolotlik.axolotlikcosmocats.repository.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.ProductRepository;
import org.axolotlik.axolotlikcosmocats.repository.entity.CategoryEntity;
import org.axolotlik.axolotlikcosmocats.repository.entity.ProductEntity;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("CategoryController integration tests")
class CategoryControllerIT extends AbstractIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;

  @SpyBean private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    reset(categoryService);
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all categories (200 OK)")
  void shouldGetAllCategories() {
    categoryRepository.save(
        new CategoryEntity(null, "Clothing", "Cosmic fashion", new ArrayList<>()));
    categoryRepository.save(
        new CategoryEntity(null, "Food", "Interstellar snacks", new ArrayList<>()));

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categories").isArray())
        .andExpect(jsonPath("$.categories.length()").value(2))
        .andExpect(jsonPath("$.categories[?(@.name == 'Clothing')]").exists())
        .andExpect(jsonPath("$.categories[?(@.description == 'Cosmic fashion')]").exists())
        .andExpect(jsonPath("$.categories[?(@.name == 'Food')]").exists())
        .andExpect(jsonPath("$.categories[?(@.description == 'Interstellar snacks')]").exists());

    verify(categoryService).getAllCategories();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get category by id (200 OK)")
  void shouldGetCategoryById() {
    var savedCategory =
        categoryRepository.save(
            new CategoryEntity(null, "Clothing", "Cosmic fashion", new ArrayList<>()));

    mockMvc
        .perform(get("/api/v1/categories/{id}", savedCategory.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedCategory.getId()))
        .andExpect(jsonPath("$.name").value("Clothing"))
        .andExpect(jsonPath("$.description").value("Cosmic fashion"));

    verify(categoryService).getCategoryById(savedCategory.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when category not found")
  void shouldReturnNotFoundForMissingCategory() {
    long id = 9999L;
    mockMvc
        .perform(get("/api/v1/categories/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Category", id)));

    verify(categoryService).getCategoryById(id);
  }

  @Test
  @SneakyThrows
  @DisplayName("should create new category (201 Created)")
  void shouldCreateCategory() {
    CategoryRequestDto request =
        CategoryRequestDto.builder().name("Toys").description("For space cats").build();

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Toys"))
        .andExpect(jsonPath("$.description").value("For space cats"));

    verify(categoryService).createCategory(any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 400 for invalid category request (missing name)")
  void shouldReturnBadRequestForInvalidCategory() {
    CategoryRequestDto request =
        CategoryRequestDto.builder().description("No name provided").build();

    mockMvc
        .perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.type").value("validation-error"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0]").exists())
        .andExpect(jsonPath("$.errors[0].field").value("name"))
        .andExpect(jsonPath("$.errors[0].reason").value("must not be blank"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should update category (200 OK)")
  void shouldUpdateCategory() {
    var savedCategory =
        categoryRepository.save(new CategoryEntity(null, "OldName", "OldDesc", new ArrayList<>()));

    CategoryRequestDto update =
        CategoryRequestDto.builder().name("Updated").description("NewDesc").build();

    mockMvc
        .perform(
            put("/api/v1/categories/{id}", savedCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedCategory.getId()))
        .andExpect(jsonPath("$.name").value("Updated"))
        .andExpect(jsonPath("$.description").value("NewDesc"));

    verify(categoryService).updateCategory(eq(savedCategory.getId()), any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when updating missing category")
  void shouldReturnNotFoundWhenUpdatingMissingCategory() {
    long id = 12345L;
    CategoryRequestDto update =
        CategoryRequestDto.builder().name("Updated").description("NewDesc").build();

    mockMvc
        .perform(
            put("/api/v1/categories/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Category", id)));

    verify(categoryService).updateCategory(eq(id), any());
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete category (204 No Content)")
  void shouldDeleteCategory() {
    var savedCategory =
        categoryRepository.save(new CategoryEntity(null, "Temp", "To delete", new ArrayList<>()));

    mockMvc
        .perform(delete("/api/v1/categories/{id}", savedCategory.getId()))
        .andExpect(status().isNoContent());

    verify(categoryService).deleteCategory(savedCategory.getId());
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 409 Conflict when deleting category with linked products")
  void shouldReturnConflictWhenDeletingCategoryWithProducts() {
    var category =
        categoryRepository.save(
            new CategoryEntity(null, "Protected Category", "Has products", new ArrayList<>()));

    productRepository.save(new ProductEntity(null, "Linked Item", "Desc", 10.0, true, category));

    mockMvc
        .perform(delete("/api/v1/categories/{id}", category.getId()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.title").value("Data Integrity Violation"))
        .andExpect(jsonPath("$.type").value("data-integrity-error"))
        .andExpect(
            jsonPath("$.detail")
                .value("Cannot delete resource because it is referenced by other records."));

    verify(categoryService).deleteCategory(category.getId());

    org.assertj.core.api.Assertions.assertThat(categoryRepository.existsById(category.getId()))
        .isTrue();
  }
}
