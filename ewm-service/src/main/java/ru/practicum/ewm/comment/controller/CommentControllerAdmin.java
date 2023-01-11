package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/admin/comments/{commentId}")
public class CommentControllerAdmin {
    private final CommentService commentService;

    @DeleteMapping
    public void delete(@PathVariable Long commentId) {
        log.info("Получен запрос DELETE admin/comments/{}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
