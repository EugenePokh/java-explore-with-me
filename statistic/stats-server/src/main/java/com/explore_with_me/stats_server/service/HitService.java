package com.explore_with_me.stats_server.service;

import com.explore_with_me.stats_server.model.Hit;
import com.explore_with_me.stats_server.model.HitCount;
import com.explore_with_me.stats_server.repository.HitRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class HitService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HitRepository hitRepository;

    public Hit save(Hit hit) {
        logger.info("Save hit " + hit.toString());
        return hitRepository.save(hit);
    }

    public List<HitCount> findHitCounts(LocalDateTime start, LocalDateTime end) {
        return hitRepository.findHitCounts(start, end);
    }

    public List<HitCount> findHitCountsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return hitRepository.findHitCountsByUris(start, end, uris);
    }

    public List<HitCount> findHitCountsUnique(LocalDateTime start, LocalDateTime end) {
        return hitRepository.findHitCountsUnique(start, end);
    }

    public List<HitCount> findHitCountsByUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return hitRepository.findHitCountsByUrisUnique(start, end, uris);
    }
}
