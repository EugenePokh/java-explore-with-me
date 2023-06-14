package com.explorewithme.statsserver.controller;

import com.explorewithme.dto.HitCountResponseDto;
import com.explorewithme.dto.HitRequestDto;
import com.explorewithme.statsserver.mapper.HitMapper;
import com.explorewithme.statsserver.model.*;
import com.explorewithme.statsserver.service.HitService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class StatisticController {

    private final HitService hitService;

    @PostMapping("/hit")
    public ResponseEntity<Void> hit(@Valid @RequestBody HitRequestDto hitDto) {
        Hit hit = HitMapper.toModel(hitDto);
        hitService.save(hit);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/stats")
    public List<HitCountResponseDto> stats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return hitService.findHitCounts(start, end, uris, unique);

    }

}
