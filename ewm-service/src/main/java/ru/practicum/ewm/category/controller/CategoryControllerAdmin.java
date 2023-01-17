package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен запрос POST /admin/categories");
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return categoryService.create(category);
    }

    @PatchMapping
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Получен запрос PATCH /admin/categories");
        Category category = CategoryMapper.toCategory(categoryDto);
        return categoryService.update(category);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        log.info("Получен запрос DELETE /admin/categories/{}", categoryId);
        categoryService.delete(categoryId);
    }
}
