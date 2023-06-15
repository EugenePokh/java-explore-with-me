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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
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

        EventResponseDto dto = EventMapper.toDto(eventService.findById(eventId)
                .filter(event -> event.getState() == Event.State.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId)));

        long hits = client.findHitCountsByUrisUnique(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1),
                        Arrays.asList("/events/" + dto.getId()))
                .get(0)
                .getHits();

        dto.setViews(hits);

        dto.setViews(1L);

        return dto;
    }

    @GetMapping("/events")
    public List<EventResponseDto> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Integer> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) Boolean onlyAvailable,
                                          @RequestParam(required = false) EventSort sort,
                                          HttpServletRequest httpServletRequest) {

        client.saveStatistic(
                HitRequestDto.builder()
                        .app(appName)
                        .ip(httpServletRequest.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .uri(httpServletRequest.getRequestURI())
                        .build());

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("rangeEnd can not be before rangeStart");
            }
        }


        Sort sortValue = Sort.by("eventDate").descending();
        if (sort == EVENT_DATE) {
            sortValue = Sort.by("eventDate").descending();
        }

        PageRequest page = PageRequest.of(from / size, size, sortValue);
        List<Event> events = eventService.findAllBySearch(text, categories, paid, onlyAvailable, rangeStart, rangeEnd, page).getContent();
        return events
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    public enum EventSort {
        EVENT_DATE, VIEWS
    }
}
