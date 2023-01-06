package ru.practicum.ewm.cetegoryTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    private Category category;

    private CategoryDto categoryDto;

    @BeforeEach
    void beforeEach() {
        category = Category.builder()
                .id(1L)
                .name("Концерты")
                .build();

        categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Концерты")
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(categoryRepository.save(any()))
                .thenReturn(category);
        CategoryDto newCategory = categoryService.create(category);

        assertEquals(categoryDto, newCategory);

        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void update() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(category));
        Mockito
                .when(categoryRepository.save(any()))
                .thenReturn(category);
        category.setName("Развлечения");
        CategoryDto updateCategory = categoryService.update(category);

        assertEquals("Развлечения", updateCategory.getName());

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void updateFail() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(category));

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAll() {
        Mockito
                .when(categoryRepository.findAll((Pageable) any()))
                .thenReturn(new PageImpl<>(List.of(category)));
        List<CategoryDto> getCategories = categoryService.getAll(0, 10);

        assertEquals(1, getCategories.size());

        verify(categoryRepository, times(1)).findAll((Pageable) any());
    }

    @Test
    void get() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(category));
        CategoryDto getCategory = categoryService.get(1L);

        assertEquals(categoryDto, getCategory);

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    void getFail() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.get(category.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    public void delete() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(category));
        categoryService.delete(category.getId());
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(category.getId()));

        verify(categoryRepository, times(2)).findById(anyLong());
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void deleteFail() {
        Mockito
                .when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(category.getId()));

        verify(categoryRepository, times(1)).findById(anyLong());
    }
}
