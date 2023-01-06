package ru.practicum.stats.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.service.StatsService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class StatController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void addHit(@RequestBody EndpointHit endpointHit) {
        log.info("Получен запрос POST /hit");
        statsService.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получен запрос GET /stats");
        return statsService.getStats(start, end, uris, unique);
    }
}
