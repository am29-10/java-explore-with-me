package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.Status;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequestor_IdAndEvent_Id(Long requestorId, Long eventId);

    Long countByEvent_IdAndStatus(Long eventId, Status status);

    List<Request> findAllByEvent_IdAndStatus(Long eventId, Status status);

    List<Request> findAllByRequestor_Id(Long requestorId);

    List<Request> findAllByEvent_Id(Long eventId);

}
