package com.explore_with_me.stats_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HitCountResponse {

    private String app;
    private String uri;
    private Long hits;

}
