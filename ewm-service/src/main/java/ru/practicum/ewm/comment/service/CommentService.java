package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentService {

    CommentDto create(Long authorId, Long eventId, Comment comment);

    CommentDto update(Long authorId, Long eventId, Long commentId, Comment comment);

    void delete(Long authorId, Long eventId, Long commentId);

    List<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> searchByText(Long authorId, Long eventId, String text, Integer from, Integer size);

    void deleteByAdmin(Long commentId);

}
