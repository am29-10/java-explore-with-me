package ru.practicum.ewm.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto create(Long authorId, Long eventId, Comment comment) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        User author = userRepository.findById(authorId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d отсутствует в базе", authorId)));
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        log.info("Комментарий с id = {} создан", comment.getId());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(Long authorId, Long eventId, Long commentId, Comment comment) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        userRepository.findById(authorId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d отсутствует в базе", authorId)));
        Comment commentById = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Комментарий с id = %d отсутствует в базе", commentId)));
        if (!commentId.equals(authorId)) {
            throw new IllegalArgumentException("Нельзя обновить чужой комментарий");
        }
        commentById.setText(comment.getText());
        log.info("Комментарий с id = {} обновлен", commentById.getId());
        return CommentMapper.toCommentDto(commentRepository.save(commentById));
    }

    @Override
    public void delete(Long authorId, Long eventId,Long commentId) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        userRepository.findById(authorId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d отсутствует в базе", authorId)));
        commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Комментарий с id = %d отсутствует в базе", commentId)));
        if (!commentId.equals(authorId)) {
            throw new IllegalArgumentException("Нельзя удалить чужой комментарий");
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} удален", commentId);
    }

    @Override
    public List<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEvent_Id(eventId, pageable).toList();
        log.info("Получен список комментариев для события с id = {}", eventId);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> searchByText(Long authorId, Long eventId, String text, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        userRepository.findById(authorId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d отсутствует в базе", authorId)));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByText(text, pageable).toList();
        log.info("Получен список комментариев с text = {}", text);
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByAdmin(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Комментарий с id = %d отсутствует в базе", commentId)));
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} удален", commentId);
    }
}
