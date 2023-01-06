package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {

    private final EventService eventService;

    @PostMapping
    public EventFullDto create(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос POST /users/{}/events", userId);
        Event event = EventMapper.toEvent(newEventDto);
        return eventService.create(userId, event);
    }

    @PatchMapping
    public EventFullDto update(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Получен запрос PATCH /users/{}/events", userId);
        Event event = EventMapper.toEvent(newEventDto);
        return eventService.update(userId, event);
    }

    @GetMapping
    public List<EventShortDto> getAllByUserId(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /users/{}/events", userId);
        return eventService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserId(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}", userId, eventId);
        return eventService.getByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancel(@PathVariable Long userId,
                               @PathVariable Long eventId) {
        log.info("Получен запрос PATCH /users/{}/events/{}", userId, eventId);
        return eventService.cancel(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Получен запрос GET /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId) {
        log.info("Получен запрос PATCH /users/{}/events/{}/requests/{}/confirm", userId, eventId, reqId);
        return eventService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId) {
        log.info("Получен запрос PATCH /users/{}/events/{}/requests/{}/reject", userId, eventId, reqId);
        return eventService.cancelRequest(userId, eventId, reqId);
    }
}
