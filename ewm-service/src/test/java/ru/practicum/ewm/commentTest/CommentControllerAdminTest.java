package ru.practicum.ewm.commentTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.comment.controller.CommentControllerAdmin;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentControllerAdmin.class)
public class CommentControllerAdminTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        User author = User.builder()
                .id(1L)
                .name("Артур")
                .email("artur99@mail.ru")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Кино")
                .build();
        Location location = Location.builder()
                .id(1L)
                .lat(59.98365F)
                .lon(30.1944F)
                .build();

        Event event = Event.builder()
                .id(1L)
                .annotation("Кино под открытым небом")
                .category(category)
                .description("Показ фильма 'Аватар' в парке 300-летия на траве под открытым небом")
                .location(location)
                .paid(false)
                .participantLimit(0L)
                .requestModeration(false)
                .title("Аватар на природе")
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .author(author)
                .event(event)
                .text("Схожу на это событие еще раз")
                .build();
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/admin/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteByAdmin(anyLong());
    }
}
