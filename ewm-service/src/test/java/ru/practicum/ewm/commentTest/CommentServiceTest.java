package ru.practicum.ewm.commentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

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
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private EventRepository eventRepository;
    @MockBean
    private UserRepository userRepository;

    private Comment comment;
    private User author;
    private Event event;
    private CommentDto commentDto;


    @BeforeEach
    void beforeEach() {
        author = User.builder()
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

        event = Event.builder()
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

        comment = Comment.builder()
                .id(1L)
                .author(author)
                .event(event)
                .text("Схожу на это событие еще раз")
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .author(author)
                .event(event)
                .text("Схожу на это событие еще раз")
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        Mockito
                .when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto newComment = commentService.create(author.getId(), event.getId(), comment);
        newComment.setCreatedOn(null);

        assertEquals(commentDto, newComment);

        verify(userRepository, times(1)).findById(anyLong());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void update() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        Mockito
                .when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));
        Mockito
                .when(commentRepository.findById(anyLong()))
                        .thenReturn(Optional.of(comment));
        Mockito
                .when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto updatedComment = commentService.update(author.getId(), event.getId(), comment.getId(), comment);
        updatedComment.setCreatedOn(null);

        assertEquals(commentDto, updatedComment);

        verify(userRepository, times(1)).findById(anyLong());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void delete() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        Mockito
                .when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));
        Mockito
                .when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));

        commentService.delete(author.getId(), event.getId(), comment.getId());

        Mockito
                .when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> commentService.delete(author.getId(), event.getId(), comment.getId()));

        verify(userRepository, times(2)).findById(anyLong());
        verify(eventRepository, times(2)).findById(anyLong());
        verify(commentRepository, times(2)).findById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void getAllByEventId() {
        Mockito
                .when(commentRepository.findAllByEvent_Id(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(comment)));

        List<CommentDto> comments = commentService.getAllByEventId(event.getId(), 1, 10);

        assertEquals(1, comments.size());

        verify(commentRepository, times(1)).findAllByEvent_Id(anyLong(), any());
    }

    @Test
    void searchByText() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        Mockito
                .when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));
        Mockito
                .when(commentRepository.findAllByText(any(), any()))
                .thenReturn(new PageImpl<>(List.of(comment)));

        List<CommentDto> comments = commentService.searchByText(author.getId(), event.getId(), "Text", 1, 10);

        assertEquals(1, comments.size());

        verify(commentRepository, times(1)).findAllByText(any(), any());
    }

    @Test
    void deleteByAdmin() {
        Mockito
                .when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));

        commentService.deleteByAdmin(comment.getId());

        Mockito
                .when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        assertThrows(EntityNotFoundException.class, () -> commentService.deleteByAdmin(comment.getId()));

        verify(commentRepository, times(1)).deleteById(anyLong());
    }
}
