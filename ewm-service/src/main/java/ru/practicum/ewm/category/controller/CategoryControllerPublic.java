package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/categories")
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /categories?from={}&size={}", from, size);
        return categoryService.getAll(from, size);

    }

    @GetMapping("/{categoryId}")
    public CategoryDto get(@PathVariable Long categoryId) {
        log.info("Получен запрос GET /categories/{}", categoryId);
        return categoryService.get(categoryId);
    }
}
