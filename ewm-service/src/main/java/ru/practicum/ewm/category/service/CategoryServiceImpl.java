package ru.practicum.ewm.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ConflictException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new ConflictException(String.format("Категория с названием %s уже есть в базе", category.getName()));
        }
        Category createCategory = categoryRepository.save(category);
        log.info("Категория с id = {} создана", category.getId());
        return CategoryMapper.toCategoryDto(createCategory);
    }

    @Override
    @Transactional
    public CategoryDto update(Category category) {
        Category updateCategory = categoryRepository.findById(category.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Категория с id = %d не может быть обновлена, т.к. она " +
                        "отсутствует в базе", category.getId())));
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new ConflictException(String.format("Категория с названием %s уже есть в базе", category.getName()));
        }
        updateCategory.setName(category.getName());
        log.info("Категория с id = {} обновлена", category.getId());
        return CategoryMapper.toCategoryDto(categoryRepository.save(updateCategory));
    }

    @Override
    @Transactional
    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).toList();
        log.info("Получен список всех категорий");
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto get(Long categoryId) {
        Category getCategory = categoryRepository.findById(categoryId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Категория с id = %d не может быть получена, т.к. она " +
                        "отсутствует в базе", categoryId)));
        log.info("Получена категория с id = {}", categoryId);
        return CategoryMapper.toCategoryDto(getCategory);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Категория с id = %d не может быть удалена, т.к. она " +
                        "отсутствует в бд", categoryId)));
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id = {} удалена", categoryId);
    }
}
