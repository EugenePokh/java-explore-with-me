package com.explore_with_me.stats_server.controller;

import com.explore_with_me.stats_server.dto.HitRequest;
import com.explore_with_me.stats_server.dto.HitCountResponse;
import com.explore_with_me.stats_server.model.Hit;
import com.explore_with_me.stats_server.model.HitCount;
import com.explore_with_me.stats_server.service.HitService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Validated
public class StatisticController {

    private final HitService hitService;

    @PostMapping("/hit")
    public ResponseEntity<Void> hit(@Valid @RequestBody HitRequest hitDto) {
        Hit hit = toModel(hitDto);
        hitService.save(hit);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/stats")
    public List<HitCountResponse> stats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        List<HitCount> hitCounts;

        if (Objects.nonNull(uris) && !uris.isEmpty() && !unique) {
            hitCounts = hitService.findHitCountsByUris(start, end, uris);
        } else if (Objects.nonNull(uris) && !uris.isEmpty() && unique) {
            hitCounts = hitService.findHitCountsByUrisUnique(start, end, uris);
        } else if (unique) {
            hitCounts = hitService.findHitCountsUnique(start, end);
        } else {
            hitCounts = hitService.findHitCounts(start, end);
        }

        return hitCounts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Hit toModel(HitRequest hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    private HitCountResponse toDto(HitCount hitCount) {
        return HitCountResponse.builder()
                .app(hitCount.getApp())
                .uri(hitCount.getUri())
                .hits(hitCount.getHits())
                .build();
    }
}
