package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.Status;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Запрос " +
                "на данное событие не может быть создан, т.к пользователь с id = %d отсутствует в базе", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Запрос на " +
                "событие не может быть создан, т.к данное событие отсутствует в базе"));
        if (userId.equals(event.getInitiator().getId())) {
            throw new IllegalArgumentException("Запрос не может быть создан, т.к пользователь является инициатором " +
                    "данного события");
        }
        if (requestRepository.findByRequestor_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new IllegalArgumentException("Запрос для данного события уже был создан");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new IllegalArgumentException("Запрос может быть создан только для опубликовнных событий");
        }
        if (requestRepository.countByEvent_IdAndStatus(eventId, Status.CONFIRMED).equals(event.getParticipantLimit()) &&
                event.getParticipantLimit() != 0) {
            throw new IllegalArgumentException("Запрос на событие не может быть создан, т.к. лимит заявок был исчерпан");
        }
        Request request = Request.builder()
                .createdOn(LocalDateTime.now())
                .requestor(user)
                .event(event)
                .build();
        if (event.getRequestModeration()) {
            request.setStatus(Status.PENDING);
        } else {
            request.setStatus(Status.CONFIRMED);
        }
        log.info("Создан запрос пользователя с id = {} на участие в событии с id = {}", userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Запрос " +
                "на данное событие не может быть отменён, т.к пользователь с id = %d отсутствует в базе", userId)));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запрос на событие не может быть отменён, т.к запрос с " +
                        "id = %d отсутствует в базе", requestId)));
        if (!userId.equals(request.getRequestor().getId())) {
            throw new IllegalArgumentException("Запрос не может быть отменён, т.к пользователь не является создателем " +
                    "данного запроса");
        }
        request.setStatus(Status.CANCELED);
        log.info("Запрос с id = {} был отклонён", requestId);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Запрос " +
                "на данное событие не может быть отменён, т.к пользователь с id = %d отсутствует в базе", userId)));
        List<Request> requests = requestRepository.findAllByRequestor_Id(userId);
        log.info("Получен список всех запросов, созданных пользователем с id = {}", userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
