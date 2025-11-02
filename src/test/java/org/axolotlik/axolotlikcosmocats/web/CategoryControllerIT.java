package org.axolotlik.axolotlikcosmocats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryRequestDto;
import org.axolotlik.axolotlikcosmocats.repository.impl.CategoryRepository;
import org.axolotlik.axolotlikcosmocats.repository.impl.ProductRepository;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.axolotlik.axolotlikcosmocats.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.axolotlik.axolotlikcosmocats.service.exception.NotFoundException.ID_NOT_FOUND;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CategoryController integration tests")
class CategoryControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;

  @SpyBean private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    reset(categoryService);
    categoryRepository.clear();
    productRepository.clear();
  }

  @Test
  @SneakyThrows
  @DisplayName("should get all categories (200 OK)")
  void shouldGetAllCategories() {
    categoryRepository.save(1L, new Category(1L, "Clothing", "Cosmic fashion"));
    categoryRepository.save(2L, new Category(2L, "Food", "Interstellar snacks"));

    mockMvc
        .perform(get("/api/v1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categories").isArray())
        .andExpect(jsonPath("$.categories.length()").value(2))
        .andExpect(jsonPath("$.categories[0].id").value(1))
        .andExpect(jsonPath("$.categories[0].name").value("Clothing"))
        .andExpect(jsonPath("$.categories[0].description").value("Cosmic fashion"))
        .andExpect(jsonPath("$.categories[1].id").value(2))
        .andExpect(jsonPath("$.categories[1].name").value("Food"))
        .andExpect(jsonPath("$.categories[1].description").value("Interstellar snacks"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should get category by id (200 OK)")
  void shouldGetCategoryById() {
    categoryRepository.save(1L, new Category(1L, "Clothing", "Cosmic fashion"));

    mockMvc
        .perform(get("/api/v1/categories/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Clothing"))
        .andExpect(jsonPath("$.description").value("Cosmic fashion"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when category not found")
  void shouldReturnNotFoundForMissingCategory() {
    long id = 99L;
    mockMvc
        .perform(get("/api/v1/categories/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.type").value("not-found"))
        .andExpect(jsonPath("$.detail").value(String.format(ID_NOT_FOUND, "Category", id)));
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
    categoryRepository.save(1L, new Category(1L, "OldName", "OldDesc"));

    CategoryRequestDto update =
        CategoryRequestDto.builder().name("Updated").description("NewDesc").build();

    mockMvc
        .perform(
            put("/api/v1/categories/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Updated"))
        .andExpect(jsonPath("$.description").value("NewDesc"));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return 404 when updating missing category")
  void shouldReturnNotFoundWhenUpdatingMissingCategory() {
    long id = 123L;
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
  }

  @Test
  @SneakyThrows
  @DisplayName("should delete category (204 No Content)")
  void shouldDeleteCategory() {
    categoryRepository.save(5L, new Category(5L, "Temp", "To delete"));

    mockMvc.perform(delete("/api/v1/categories/{id}", 5L)).andExpect(status().isNoContent());
  }
}
