package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

// Private
    EventFullDto create(Long userId, Event event);

    EventFullDto update(Long userId, Event event);

    List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size);

    EventFullDto getByUserId(Long userId, Long eventId);

    EventFullDto cancel(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId, Long reqId);

// Admin
    List<EventFullDto> getAll(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                              String rangeEnd, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, Event event);

    EventFullDto publish(Long eventId);

    EventFullDto cancel(Long eventId);

// Public
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable, String sort,
                                  Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);


}
