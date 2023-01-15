package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.State;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);

    Page<Event> findAllByInitiator_IdInAndStateInAndCategory_IdInAndEventDateBetween(List<Long> users,
                                                                                     List<State> states,
                                                                                     List<Long> categories,
                                                                                     LocalDateTime rangeStart,
                                                                                     LocalDateTime rangeEnd,
                                                                                     Pageable pageable);


    List<Event> findAllByIdIn(List<Long> eventIds);

}
