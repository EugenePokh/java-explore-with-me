package com.explorewithme.server.controller;

import com.explorewithme.server.dto.CommentResponseDto;
import com.explorewithme.server.dto.EventResponseFullDto;
import com.explorewithme.server.dto.EventResponseShortDto;
import com.explorewithme.server.service.CommentService;
import com.explorewithme.server.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    public EventResponseFullDto findById(@PathVariable Integer eventId,
                                         HttpServletRequest httpServletRequest) {
        return eventService.findById(eventId, httpServletRequest);
    }

    @GetMapping("/events")
    public List<EventResponseShortDto> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Integer> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(required = false) EventSort sort,
                                               HttpServletRequest httpServletRequest) {
        return eventService.findAllBySearch(text, categories, paid, onlyAvailable, rangeStart, rangeEnd, sort, from, size, httpServletRequest)
                .getContent();
    }

    public enum EventSort {
        EVENT_DATE, VIEWS
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentResponseDto> findCommentByEvent(@PathVariable Integer eventId) {
        return commentService.findByEventId(eventId);
    }
}
