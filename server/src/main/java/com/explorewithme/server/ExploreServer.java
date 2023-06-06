package com.explorewithme.server;

import com.explorewithme.statsclient.StatsClientConfig;
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
