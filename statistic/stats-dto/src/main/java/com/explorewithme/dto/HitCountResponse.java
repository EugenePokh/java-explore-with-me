package com.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HitCountResponse {

    private String app;
    private String uri;
    private Long hits;

}
