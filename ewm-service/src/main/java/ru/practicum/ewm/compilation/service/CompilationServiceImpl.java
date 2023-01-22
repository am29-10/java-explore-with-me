package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(Compilation compilation) {
        if (compilationRepository.findByTitle(compilation.getTitle()).isPresent()) {
            throw new ConflictException(String.format("Подборка событий с названием %s уже есть в базе",
                    compilation.getTitle()));
        }
        List<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        compilation.setEvents(eventRepository.findAllByIdIn(eventIds));
        log.info("Подборка событий создана");
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));

    }

    @Override
    public void delete(Long compilationId) {
        compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        compilationRepository.deleteById(compilationId);
        log.info("Подборка событий удалена");
    }

    @Override
    public void deleteEventFromCompilation(Long compilationId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        compilation.getEvents().remove(event);
        log.info("Событие с id = {} удалено из подборки", eventId);
        compilationRepository.save(compilation);
    }

    @Override
    public void addEvent(Long compilationId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Событие с id = %d отсутствует в базе", eventId)));
        if (compilation.getEvents().contains(event)) {
            throw new ConflictException(String.format("Событие с id = %d уже есть в подборке", eventId));
        }
        compilation.getEvents().add(event);
        log.info("Событие с id = {} добавлено в подборку событий с id = {}", eventId, compilationId);
        compilationRepository.save(compilation);
    }

    @Override
    public void unpinCompilation(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        compilation.setPinned(false);
        log.info("Подборка снята с главной странице");
        compilationRepository.save(compilation);
    }

    @Override
    public void pinCompilation(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        compilation.setPinned(true);
        log.info("Подборка закреплена на главной странице");
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable).toList();
        log.info("Получен список подборок");
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto get(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Подборка событий с id = %d отсутствует в базе", compilationId)));
        log.info("Получена подборка событий с id = {}", compilationId);
        log.info("Количество событий в подборке равно {}", compilationRepository.findAll().get(0).getEvents().size());
        return CompilationMapper.toCompilationDto(compilation);
    }
}
