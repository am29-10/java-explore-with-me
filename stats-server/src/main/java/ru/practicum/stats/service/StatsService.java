package ru.practicum.stats.service;

import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.util.List;

public interface StatsService {

    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
}
