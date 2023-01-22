package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен запрос POST users/{}/events/{}/comments", userId, eventId);
        Comment comment = CommentMapper.toComment(newCommentDto);
        return commentService.create(userId, eventId, comment);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @PathVariable Long commentId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Получен запрос PATCH users/{}/events/{}/comments/{}", userId, eventId, commentId);
        Comment comment = CommentMapper.toComment(newCommentDto);
        return commentService.update(userId, eventId, commentId, comment);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long userId,
                       @PathVariable Long eventId,
                       @PathVariable Long commentId) {
        log.info("Получен запрос DELETE users/{}/events/{}/comments/{}", userId, eventId, commentId);
        commentService.delete(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentDto> searchByText(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /users/{}/events/{}/comments?text={}", userId, eventId, text);
        return commentService.searchByText(userId, eventId, text, from, size);
    }
}
