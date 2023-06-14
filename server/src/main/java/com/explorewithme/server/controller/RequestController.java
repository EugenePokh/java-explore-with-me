package com.explorewithme.server.controller;

import com.explorewithme.server.dto.RequestResponseDto;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.exception.RequestNotFoundException;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.RequestMapper;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import com.explorewithme.server.service.EventService;
import com.explorewithme.server.service.RequestService;
import com.explorewithme.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;
    private final EventService eventService;

    @GetMapping
    public List<RequestResponseDto> findAll(@PathVariable Integer userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        return requestService.findAllByRequestor(user)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<RequestResponseDto> post(@PathVariable Integer userId,
                                                   @RequestParam Integer eventId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId));

        RequestResponseDto responseDto = RequestMapper.toDto(requestService.create(user, event));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PatchMapping("{requestId}/cancel")
    public RequestResponseDto patch(@PathVariable Integer userId,
                         @PathVariable Integer requestId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));

        Request request = requestService.findAllRequestorAndId(user, requestId)
                .orElseThrow(() -> new RequestNotFoundException("No such user with id - " + userId));;

        return RequestMapper.toDto(requestService.cancel(request));
    }

}
