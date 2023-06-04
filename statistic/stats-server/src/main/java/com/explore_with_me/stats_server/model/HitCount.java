package com.explore_with_me.stats_server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitCount {
    private String app;
    private String uri;
    private Long hits;
}
