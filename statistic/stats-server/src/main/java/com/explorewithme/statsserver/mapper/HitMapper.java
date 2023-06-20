package com.explorewithme.statsserver.mapper;

import com.explorewithme.dto.HitRequestDto;
import com.explorewithme.statsserver.model.Hit;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HitMapper {

    public Hit toModel(HitRequestDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
