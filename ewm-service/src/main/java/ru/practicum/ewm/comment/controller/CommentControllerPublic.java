package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllByEventId(@PathVariable Long eventId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET events/{}/comments", eventId);
        return commentService.getAllByEventId(eventId, from, size);
    }

}
