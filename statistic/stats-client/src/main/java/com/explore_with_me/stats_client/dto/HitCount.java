package com.explore_with_me.stats_client.dto;

import lombok.Data;

@Data
public class HitCount {
    private String app;
    private String uri;
    private Long hits;
}