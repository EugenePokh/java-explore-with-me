package com.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HitCountResponseDto {

    private String app;
    private String uri;
    private Long hits;

}
