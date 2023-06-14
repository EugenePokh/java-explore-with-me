package com.explorewithme.statsserver.service;

import com.explorewithme.dto.HitCountResponseDto;
import com.explorewithme.statsserver.model.Hit;
import com.explorewithme.statsserver.repository.HitRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class HitService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HitRepository hitRepository;

    public Hit save(Hit hit) {
        logger.info("Save hit " + hit.toString());
        return hitRepository.save(hit);
    }

    public List<HitCountResponseDto> findHitCounts(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<HitCountResponseDto> hitCounts;
        if (Objects.nonNull(uris) && !uris.isEmpty() && !unique) {
            hitCounts = hitRepository.findHitCountsByUris(start, end, uris);
        } else if (Objects.nonNull(uris) && !uris.isEmpty() && unique) {
            hitCounts = hitRepository.findHitCountsByUrisUnique(start, end, uris);
        } else if (unique) {
            hitCounts = hitRepository.findHitCountsUnique(start, end);
        } else {
            hitCounts = hitRepository.findHitCounts(start, end);
        }

        return hitCounts;
    }

}
