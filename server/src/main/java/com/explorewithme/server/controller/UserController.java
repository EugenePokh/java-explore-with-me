package com.explorewithme.server.controller;

import com.explorewithme.server.dto.*;
import com.explorewithme.server.service.EventService;
import com.explorewithme.server.service.RequestService;
import com.explorewithme.server.validation.annotation.CommonCheck;
import com.explorewithme.server.validation.annotation.AdvancedCheck;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class UserController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<EventResponseFullDto> postEvent(@PathVariable Integer userId,
                                                          @RequestBody @Validated({AdvancedCheck.class, CommonCheck.class}) EventRequestDto eventRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.post(userId, eventRequestDto));
    }

    @GetMapping
    public List<EventResponseShortDto> findAllEvents(@PathVariable Integer userId,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.findAllByAuthor(userId, from, size)
                .getContent();
    }

    @GetMapping("/{eventId}")
    public EventResponseFullDto findEventById(@PathVariable Integer userId,
                                              @PathVariable Integer eventId) {
        return eventService.findByAuthorAndId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventResponseFullDto patchEvent(@PathVariable Integer userId,
                                           @PathVariable Integer eventId,
                                           @RequestBody @Validated({CommonCheck.class}) EventRequestDto eventRequestDto) {
        return eventService.patch(userId, eventId, eventRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestResponseDto> findAllRequests(@PathVariable Integer userId,
                                                    @PathVariable Integer eventId) {
        return requestService.findAllByRequesterAndEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public ChangeStateResponseDto changeState(@PathVariable Integer userId,
                                              @PathVariable Integer eventId,
                                              @RequestBody @Valid ChangeStateRequestDto changeStateDto) {
        return requestService.changeState(userId, eventId, changeStateDto);
    }

}
