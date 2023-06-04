package com.explore_with_me.server.controller;

import com.explore_with_me.stats_client.StatsClient;
import com.explore_with_me.stats_client.dto.HitCount;
import com.explore_with_me.stats_client.dto.HitRequest;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class HitController {

    private final StatsClient statsClient;

    @PostMapping
    public void saveStatistic(HitRequest item) {
        statsClient.saveStatistic(item);
    }

    @GetMapping
    public List<HitCount> getHitcount(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return statsClient.findStatisticUnique(start, end);
    }
}
