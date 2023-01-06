package ru.practicum.ewm.category.service;


import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto create(Category category);

    CategoryDto update(Category category);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto get(Long categoryId);

    void delete(Long categoryId);


}
