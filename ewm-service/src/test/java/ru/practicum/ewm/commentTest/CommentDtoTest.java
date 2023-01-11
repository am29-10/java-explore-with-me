package ru.practicum.ewm.commentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    @Autowired
    private JacksonTester<Comment> jsonComment;

    private CommentDto commentDto;
    private Comment comment;
    private NewCommentDto newCommentDto;

    @BeforeEach
    void beforeEach() {
        commentDto = CommentDto.builder()
                .id(1L)
                .text("Хорошее событие, обязательно схожу")
                .build();

        comment = Comment.builder()
                .id(2L)
                .text("Был на этом событии с друзьями, очень понравилось")
                .build();

        newCommentDto = NewCommentDto.builder()
                .text("Событие подойдет для детей в возрасте 7-10 лет?")
                .build();
    }

    @Test
    void toCommentDto() throws IOException {
        commentDto = CommentMapper.toCommentDto(comment);
        JsonContent<CommentDto> result = jsonCommentDto.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Был на этом событии с " +
                "друзьями, очень понравилось");
    }

    @Test
    void toComment() throws IOException {
        comment = CommentMapper.toComment(newCommentDto);
        JsonContent<Comment> result = jsonComment.write(comment);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Событие подойдет для " +
                "детей в возрасте 7-10 лет?");
    }

}
