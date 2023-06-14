package com.explorewithme.server.controller;

import com.explorewithme.dto.HitRequestDto;
import com.explorewithme.server.dto.EventResponseDto;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.mapper.EventMapper;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.service.EventService;
import com.explorewithme.statsclient.StatsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.explorewithme.server.controller.EventController.EventSort.EVENT_DATE;

@RestController
public class EventController {

    @Value("${spring.application.name}")
    private String appName;
    private final EventService eventService;
    private final StatsClient client;

    public EventController(EventService eventService, StatsClient client) {
        this.eventService = eventService;
        this.client = client;
    }

    @GetMapping("/events/{eventId}")
    public EventResponseDto findById(@PathVariable Integer eventId,
                                     HttpServletRequest httpServletRequest) {
        client.saveStatistic(HitRequestDto.builder()
                .app(appName)
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .uri(httpServletRequest.getRequestURI())
                .build());
        return EventMapper.toDto(eventService.findById(eventId)
                .filter(event -> event.getState() == Event.State.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId)));
    }

    @GetMapping("/events")
    public List<EventResponseDto> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          @RequestParam(required = false) LocalDateTime rangeStat,
                                          @RequestParam(required = false) LocalDateTime rangeEnd,
                                          @RequestParam String text,
                                          @RequestParam(required = false) List<Integer> categories,
                                          @RequestParam Boolean paid,
                                          @RequestParam Boolean onlyAvailable,
                                          @RequestParam EventSort sort,
                                          HttpServletRequest httpServletRequest) {

        client.saveStatistic(
                HitRequestDto.builder()
                        .app(appName)
                        .ip(httpServletRequest.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .uri(httpServletRequest.getRequestURI())
                        .build());

        Sort sortValue = Sort.by("eventDate").descending();
        if (sort == EVENT_DATE) {
            sortValue = Sort.by("eventDate").descending();
        }

        PageRequest page = PageRequest.of(from / size, size, sortValue);
        return eventService.findAllBySearch(text, categories, paid, onlyAvailable, page)
                .getContent()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    public enum EventSort {
        EVENT_DATE, VIEWS
    }
}
