package com.explore_with_me.stats_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StatsClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(@Value("${stats.client.uri}") String uri,
                                   RestTemplate restTemplate) {
        return new StatsClient(uri, restTemplate);
    }
}
