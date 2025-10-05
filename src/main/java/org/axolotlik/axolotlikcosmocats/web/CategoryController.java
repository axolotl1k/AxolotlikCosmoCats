package org.axolotlik.axolotlikcosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryListResponseDto;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryRequestDto;
import org.axolotlik.axolotlikcosmocats.dto.category.CategoryResponseDto;
import org.axolotlik.axolotlikcosmocats.service.CategoryService;
import org.axolotlik.axolotlikcosmocats.service.mapper.CategoryMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public CategoryListResponseDto getAll() {
        return categoryMapper.toCategoryListResponseDto(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getById(@PathVariable Long id) {
        return categoryMapper.toCategoryResponseDto(categoryService.getCategoryById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto create(@RequestBody @Valid CategoryRequestDto dto) {
        return categoryMapper.toCategoryResponseDto(categoryService.createCategory(categoryMapper.toCategory(dto)));
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(@PathVariable Long id, @RequestBody @Valid CategoryRequestDto dto) {
        return categoryMapper.toCategoryResponseDto(categoryService.updateCategory(id, categoryMapper.toCategory(dto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

}
