package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.event.SortType;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.event.dto.EndpointHit;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.request.Status;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventClient eventClient;

    // Private
    @Override
    public EventFullDto create(Long userId, Event event) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id = %d не может организовать событие, т.к он отсутствует в базе", userId)));
        if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Событие не может быть создано, т.к. время начала события не может " +
                    "быть раньше, чем через два часа от текущего момента");
        }
        event.setCategory(categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие не может быть создано, т.к. категория %s " +
                        "отсутсвует в базе", event.getCategory().getName()))));
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        locationRepository.save(event.getLocation());
        event.setState(State.PENDING);
        event.setViews(0L);
        Event createEvent = eventRepository.save(event);
        log.info("Событие с id = {} создано", createEvent.getId());
        return EventMapper.toEventFullDto(createEvent);
    }

    @Override
    public EventFullDto update(Long userId, Event event) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие не может быть обновлено, т.к. пользователь с id = %d отсутствует в базе", userId)));
        Event updateEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Событие не может быть обновлено, т.к. оно отсутствует в базе"));
        if (!userId.equals(updateEvent.getInitiator().getId())) {
            throw new IllegalArgumentException("Событие не может быть обновлено, т.к. пользователь с id = %d не " +
                    "является организатором данного события");
        }
        if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Событие не может быть обновлено, т.к. время начала события не может " +
                    "быть раньше, чем через два часа от текущего момента");
        }
        if (updateEvent.getState() == State.CANCELED) {
            updateEvent.setState(State.PENDING);
        } else if (updateEvent.getState() == State.PUBLISHED) {
            throw new IllegalArgumentException("Событие не может быть обновлено, т.к. оно уже опубликовано");
        }
        if (event.getAnnotation() != null) {
            updateEvent.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            updateEvent.setCategory(categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Событие не может быть обновлено, т.к. категория %s " +
                            "отсутсвует в базе", event.getCategory().getName()))));
        }
        if (event.getConfirmedRequests() != null) {
            updateEvent.setConfirmedRequests(event.getConfirmedRequests());
        }
        if (event.getDescription() != null) {
            updateEvent.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            updateEvent.setEventDate(event.getEventDate());
        }
        if (event.getLocation() != null) {
            updateEvent.setLocation(event.getLocation());
        }
        if (event.getPaid() != null) {
            updateEvent.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            updateEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            updateEvent.setRequestModeration(event.getRequestModeration());
        }
        if (event.getTitle() != null) {
            updateEvent.setTitle(event.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(updateEvent));
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Список событий, созданных пользователем с id = %d не может быть получен, т.к. данный пользователь " +
                        "отсутствует в базе", userId)));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable).toList();
        log.info("Получен список событий, созданный пользователем с id = {}", userId);
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByUserId(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие, созданное пользователем с id = %d не может быть получено, т.к. данный пользователь " +
                        "отсутствует в базе", userId)));
        Event getEvent = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не может быть получено, т.к. оно отсутствует в базе", eventId)));
        log.info("Получено событие с id = {}", eventId);
        return EventMapper.toEventFullDto(getEvent);
    }

    @Override
    public EventFullDto cancel(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие, созданное пользователем с id = %d не может быть отменено, т.к. данный пользователь " +
                        "отсутствует в базе", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не может быть отменено, т.к. оно отсутствует в базе", eventId)));
        if (event.getState() != State.PENDING) {
            throw new IllegalArgumentException(String.format("Событие с id = %d не может быть отменено, т.к. оно " +
                    "не находится в состоянии ожидания модерации", eventId));
        }
        event.setState(State.CANCELED);
        log.info("Событие с id = {} отменено", eventId);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id = %d не найден", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new IllegalArgumentException("Действие не может быть совершено, т.к пользователь с id = {} не " +
                    "является создателем данного события");
        }
        List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
        log.info("Получен список запросов на участие в событии текущего пользователя");
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id = %d не найден", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        Request request = requestRepository.findById(reqId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Запрос с id = %d не найден", reqId)));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new IllegalArgumentException("Действие не может быть совершено, т.к пользователь с id = {} не " +
                    "является создателем данного события");
        }
        if (request.getStatus() == Status.PENDING) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            requestRepository.save(request);
        }
        log.info("Запрос с id = {} одобрен", reqId);
        List<Request> requests;
        if (event.getConfirmedRequests().equals(requestRepository.countByEvent_IdAndStatus(eventId, Status.CONFIRMED))) {
            requests = requestRepository.findAllByEvent_IdAndStatus(eventId, Status.PENDING);
            requests.forEach(r -> r.setStatus(Status.REJECTED));
        }
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long eventId, Long reqId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Пользователь с id = %d не найден", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        Request request = requestRepository.findById(reqId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Запрос с id = %d не найден", reqId)));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new IllegalArgumentException("Действие не может быть совершено, т.к пользователь с id = {} не " +
                    "является создателем данного события");
        }
        request.setStatus(Status.REJECTED);
        log.info("Запрос с id = {} отклонен", reqId);
        return RequestMapper.toParticipationRequestDto(request);
    }

    // Admin
    @Override
    public List<EventFullDto> getAll(List<Long> users, List<String> states, List<Long> categories,
                                     String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<State> eventStates = new ArrayList<>();
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (states != null) {
            eventStates = states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd == null) {
            endTime = LocalDateTime.now().plusYears(100);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }
        List<Event> events = eventRepository.findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateBetween(users,
                eventStates, categories, startTime, endTime, pageable).toList();
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, Event event) {
        Event updateEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        Category category = categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Категория с id = %d не найдено", event.getCategory().getId())));
        if (event.getAnnotation() != null) {
            updateEvent.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            updateEvent.setCategory(category);
        }
        if (event.getDescription() != null) {
            updateEvent.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            updateEvent.setEventDate(event.getEventDate());
        }
        if (event.getLocation() != null) {
            updateEvent.setLocation(event.getLocation());
        }
        if (event.getPaid() != null) {
            updateEvent.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            updateEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            updateEvent.setRequestModeration(event.getRequestModeration());
        }
        if (event.getTitle() != null) {
            updateEvent.setTitle(event.getTitle());
        }
        return EventMapper.toEventFullDto(eventRepository.save(updateEvent));
    }

    @Override
    public EventFullDto publish(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        if (event.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Событие не может быть опубликовано, т.к. время начала события не " +
                    "может быть раньше, чем через час от текущего момента");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new IllegalArgumentException("Событие не может быть опубликовано, т.к. для публикации событие должно " +
                    "находиться в статусе ожидания");
        }
        event.setPublishedOn(LocalDateTime.now());
        event.setState(State.PUBLISHED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancel(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Событие не может быть отменено, т.к. оно уже опубликовано");
        }
        event.setState(State.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    // Public
    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                         String rangeEnd, Boolean onlyAvailable, String sort,
                                         Integer from, Integer size, HttpServletRequest request) {

        List<Event> eventList;
        LocalDateTime startDate;
        LocalDateTime endDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Event> events = eventRepository.findAll();
        List<Category> categoriesList = categoryRepository.findAll();
        List<Long> categoriesIds;

        if (rangeStart == null) {
            startDate = LocalDateTime.now();
        } else {
            startDate = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd == null) {
            endDate = LocalDateTime.MAX;
        } else {
            endDate = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (categories == null) {
            categoriesIds = categoriesList.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        } else {
            categoriesIds = categories;
        }

        if (onlyAvailable) {
            eventList = events.stream()
                    .filter(event -> categoriesIds.contains(event.getCategory().getId())
                            && event.getAnnotation().equalsIgnoreCase(text) || event.getDescription().equalsIgnoreCase(text)
                            && event.getPaid() == paid
                            && event.getConfirmedRequests() < event.getParticipantLimit()
                            && event.getEventDate().isAfter(startDate)
                            && event.getEventDate().isBefore(endDate))
                    .collect(Collectors.toList());
        } else {
            eventList = events.stream()
                    .filter(event -> categoriesIds.contains(event.getCategory().getId())
                            && event.getAnnotation().equalsIgnoreCase(text) || event.getDescription().equalsIgnoreCase(text)
                            && event.getPaid() == paid
                            && event.getEventDate().isAfter(startDate)
                            && event.getEventDate().isBefore(endDate))
                    .collect(Collectors.toList());
        }
        for (Event event : eventList) {
            Long views = event.getViews() + 1;
            event.setViews(views);
        }
        EndpointHit endpointHit = EndpointHit.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-service")
                .timeStamp(LocalDateTime.now())
                .build();
        eventClient.addHit(endpointHit);

        if (sort != null && sort.toLowerCase().equals(SortType.VIEWS.toString().toLowerCase())) {
            return eventList.stream()
                    .map(EventMapper::toEventShortDto)
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        } else {
            return eventList.stream()
                    .map(EventMapper::toEventShortDto)
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Событие с id = %d не найдено", eventId)));
        EndpointHit endpointHit = EndpointHit.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .timeStamp(LocalDateTime.now())
                .build();
        eventClient.addHit(endpointHit);
        Long views = event.getViews() + 1;
        event.setViews(views);
        return EventMapper.toEventFullDto(event);
    }
}
