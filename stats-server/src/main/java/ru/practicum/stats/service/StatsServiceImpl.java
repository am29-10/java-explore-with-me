package ru.practicum.stats.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void addHit(EndpointHit endpointHit) {
        endpointHitRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startStat;
        LocalDateTime endStat;
        String app = "ewm-service";
        List<ViewStats> views = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        startStat = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        endStat = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);

        List<EndpointHit> endpointHits = endpointHitRepository.findAllByTimestampBetweenAndUriIn(startStat, endStat, uris);
        Long hits = (long) endpointHits.size();
        List<EndpointHit> endpointHits1 = endpointHitRepository.findDistinctByTimestampBetweenAndUriIn(startStat, endStat, uris);
        Long hits1 = (long) endpointHits1.size();

        if (unique) {
            for (String uri : uris) {
                ViewStats viewStats = ViewStats.builder()
                        .app(app)
                        .uri(uri)
                        .hits(hits1)
                        .build();
                views.add(viewStats);
            }
        } else {
            for (String uri : uris) {
                ViewStats viewStats = ViewStats.builder()
                        .app(app)
                        .uri(uri)
                        .hits(hits)
                        .build();
                views.add(viewStats);
            }
        }
        return views;
    }
}
