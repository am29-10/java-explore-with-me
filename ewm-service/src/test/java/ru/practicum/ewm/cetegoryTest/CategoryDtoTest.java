package ru.practicum.ewm.cetegoryTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CategoryDtoTest {

    @Autowired
    private JacksonTester<CategoryDto> jsonCategoryDto;

    @Autowired
    private JacksonTester<Category> jsonCategory;

    private CategoryDto categoryDto;
    private Category category;
    private NewCategoryDto newCategoryDto;

    @BeforeEach
    void beforeEach() {
        categoryDto = CategoryDto.builder()
                .id(1L)
                .name("Концерты")
                .build();

        category = Category.builder()
                .id(2L)
                .name("Развлечения")
                .build();

        newCategoryDto = NewCategoryDto.builder()
                .name("Обучение")
                .build();
    }

    @Test
    void toCategoryDto() throws IOException {
        categoryDto = CategoryMapper.toCategoryDto(category);
        JsonContent<CategoryDto> result = jsonCategoryDto.write(categoryDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Развлечения");
    }

    @Test
    void toCategoryFromCategoryDto() throws IOException {
        category = CategoryMapper.toCategory(categoryDto);
        JsonContent<Category> result = jsonCategory.write(category);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Концерты");
    }

    @Test
    void toCategoryFromNewCategoryDto() throws IOException {
        category = CategoryMapper.toCategory(newCategoryDto);
        JsonContent<Category> result = jsonCategory.write(category);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Обучение");
    }
}
