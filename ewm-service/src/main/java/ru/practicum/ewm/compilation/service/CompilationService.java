package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {

    CompilationDto create(Compilation compilation);

    void delete(Long compilationId);

    void deleteEventFromCompilation(Long compilationId, Long eventId);

    void addEvent(Long compilationId, Long eventId);

    void unpinCompilation(Long compilationId);

    void  pinCompilation(Long compilation);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto get(Long compilationId);
}
