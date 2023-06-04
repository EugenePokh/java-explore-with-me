package com.explore_with_me.stats_client;

import com.explore_with_me.stats_client.dto.HitCount;
import com.explore_with_me.stats_client.dto.HitRequest;
import com.explore_with_me.stats_client.exception.StatsClientException;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatsClient {

    private final String uri;
    private final RestTemplate restTemplate;

    public void saveStatistic(HitRequest item) {
        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/hit", item, String.class);
        if (response.getStatusCode() != (HttpStatus.CREATED)) {
            throw new StatsClientException("Post stats error - " + response.getBody());
        }
    }

    public List<HitCount> findStatisticNonUnique(LocalDateTime start,
                                                 LocalDateTime end) {
        return findStatistic(start, end, false, null);

    }

    public List<HitCount> findStatisticUnique(LocalDateTime start,
                                              LocalDateTime end) {
        return findStatistic(start, end, true, null);
    }


    public List<HitCount> findHitCountsByUrisUnique(LocalDateTime start,
                                                    LocalDateTime end,
                                                    List<String> uris) {
        return findStatistic(start, end, true, uris);
    }

    public List<HitCount> findHitCountsByUrisNonUnique(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return findStatistic(start, end, false, uris);
    }

    private List<HitCount> findStatistic(LocalDateTime start,
                                         LocalDateTime end,
                                         Boolean unique,
                                         List<String> uris) {
        URI url = buildUri(start, end, unique, uris);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HitCount>>() {
                }
        ).getBody();
    }

    private URI buildUri(LocalDateTime start,
                         LocalDateTime end,
                         Boolean unique,
                         List<String> uris) {
        String startString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(start);
        String endString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(end);
        URI url = UriComponentsBuilder.fromUriString(uri + "/stats")
                .queryParam("start", startString)
                .queryParam("end", endString)
                .queryParam("unique", unique)
                .queryParamIfPresent("uris", Optional.ofNullable(uris))
                .build()
                .toUri();

        return url;
    }
}
