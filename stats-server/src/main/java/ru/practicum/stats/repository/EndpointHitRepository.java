package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    List<EndpointHit> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uri);

    List<EndpointHit> findDistinctByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uri);

}
