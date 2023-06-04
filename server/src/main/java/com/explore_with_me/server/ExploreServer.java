package com.explore_with_me.server;

import com.explore_with_me.stats_client.StatsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(StatsClientConfig.class)
public class ExploreServer {

    public static void main(String[] args) {
        SpringApplication.run(ExploreServer.class, args);
    }
}
