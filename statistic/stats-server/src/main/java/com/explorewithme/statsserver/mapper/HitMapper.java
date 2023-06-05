package com.explorewithme.statsserver.mapper;

import com.explorewithme.dto.HitRequest;
import com.explorewithme.statsserver.model.Hit;

public class HitMapper {

    public static Hit toModel(HitRequest hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
