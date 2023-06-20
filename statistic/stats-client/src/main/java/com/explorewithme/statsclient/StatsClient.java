package com.explorewithme.statsclient;

import com.explorewithme.dto.HitCountResponseDto;
import com.explorewithme.dto.HitRequestDto;
import com.explorewithme.statsclient.exception.StatsClientException;
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

    public void saveStatistic(HitRequestDto item) {
        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/hit", item, String.class);
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new StatsClientException("Post stats error: http status code - " + response.getStatusCode() + ", response body - " + response.getBody());
        }
    }

    public List<HitCountResponseDto> findStatisticNonUnique(LocalDateTime start,
                                                            LocalDateTime end) {
        return findStatistic(start, end, false, null);

    }

    public List<HitCountResponseDto> findStatisticUnique(LocalDateTime start,
                                                         LocalDateTime end) {
        return findStatistic(start, end, true, null);
    }


    public List<HitCountResponseDto> findHitCountsByUrisUnique(LocalDateTime start,
                                                               LocalDateTime end,
                                                               List<String> uris) {
        return findStatistic(start, end, true, uris);
    }

    public List<HitCountResponseDto> findHitCountsByUrisNonUnique(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return findStatistic(start, end, false, uris);
    }

    private List<HitCountResponseDto> findStatistic(LocalDateTime start,
                                                    LocalDateTime end,
                                                    Boolean unique,
                                                    List<String> uris) {
        URI url = buildUri(start, end, unique, uris);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HitCountResponseDto>>() {
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
