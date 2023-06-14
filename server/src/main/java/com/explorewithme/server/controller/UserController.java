package com.explorewithme.server.controller;

import com.explorewithme.server.dto.*;
import com.explorewithme.server.exception.CategoryNotFoundException;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.exception.EventValidationException;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.EventMapper;
import com.explorewithme.server.mapper.RequestMapper;
import com.explorewithme.server.model.Category;
import com.explorewithme.server.model.Event;
import com.explorewithme.server.model.Request;
import com.explorewithme.server.model.User;
import com.explorewithme.server.service.CategoryService;
import com.explorewithme.server.service.EventService;
import com.explorewithme.server.service.RequestService;
import com.explorewithme.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final RequestService requestService;

    @PostMapping
    public EventResponseDto postEvent(@PathVariable Integer userId,
                                      @RequestBody @Valid EventPostRequestDto eventRequestDto) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Category category = categoryService.findById(eventRequestDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + eventRequestDto.getCategory()));

        Event event = Event.builder()
                .author(user)
                .category(category)
                .annotation(eventRequestDto.getAnnotation())
                .eventDate(eventRequestDto.getEventDate())
                .description(eventRequestDto.getDescription())
                .lat(eventRequestDto.getLocation().getLat())
                .lon(eventRequestDto.getLocation().getLon())
                .paid(eventRequestDto.getPaid())
                .participantLimit(eventRequestDto.getParticipantLimit())
                .requestModeration(eventRequestDto.getRequestModeration())
                .title(eventRequestDto.getTitle())
                .state(Event.State.PENDING)
                .created(LocalDateTime.now())
                .build();

        Event newEvent = eventService.save(event);

        return EventMapper.toDto(newEvent);
    }

    @GetMapping
    public List<EventResponseDto> findAllEvents(@PathVariable Integer userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        User author = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        PageRequest page = PageRequest.of(from / size, size);
        return eventService.findAllByAuthor(author, page)
                .getContent()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventResponseDto findEventById(@PathVariable Integer userId,
                                          @PathVariable Integer eventId) {
        User author = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        return EventMapper.toDto(eventService.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId)));

    }

    @PatchMapping("/{eventId}")
    public EventResponseDto patchEvent(@PathVariable Integer userId,
                                       @PathVariable Integer eventId,
                                       @RequestBody @Valid EventPatchRequestDto eventRequestDto) {
        User author = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventService.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        if (Objects.nonNull(eventRequestDto.getEventDate())) {
            event.setEventDate(eventRequestDto.getEventDate());
        }

        if (Objects.nonNull(eventRequestDto.getAnnotation())) {
            event.setAnnotation(eventRequestDto.getAnnotation());
        }

        if (Objects.nonNull(eventRequestDto.getRequestModeration())) {
            event.setRequestModeration(eventRequestDto.getRequestModeration());
        }

        if (Objects.nonNull(eventRequestDto.getCategory())) {
            Category cat = categoryService.findById(eventRequestDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + eventRequestDto.getCategory()));
            event.setCategory(cat);
        }

        if (Objects.nonNull(eventRequestDto.getPaid())) {
            event.setPaid(eventRequestDto.getPaid());
        }

        if (Objects.nonNull(eventRequestDto.getDescription())) {
            event.setDescription(eventRequestDto.getDescription());
        }

        if (Objects.nonNull(eventRequestDto.getTitle())) {
            event.setTitle(eventRequestDto.getTitle());
        }

        if (Objects.nonNull(eventRequestDto.getParticipantLimit())) {
            event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        }

        if (Objects.nonNull(eventRequestDto.getLocation())) {
            if (eventRequestDto.getLocation().getLat() != null) {
                event.setLat(eventRequestDto.getLocation().getLat());
            }

            if (eventRequestDto.getLocation().getLon() != null) {
                event.setLon(eventRequestDto.getLocation().getLon());
            }
        }

        if (Objects.nonNull(eventRequestDto.getStateAction())) {
            Event.State state;
            if (eventRequestDto.getStateAction() == EventPatchRequestDto.StateAction.PUBLISH_EVENT) {
                state = Event.State.PUBLISHED;
                event.setPublished(LocalDateTime.now());
            } else if (eventRequestDto.getStateAction() == EventPatchRequestDto.StateAction.REJECT_EVENT) {
                state = Event.State.CANCELED;
            } else {
                throw new IllegalArgumentException("Define behavior for status " + eventRequestDto.getStateAction());
            }
            event.setState(state);
        }

        return EventMapper.toDto(eventService.save(event));
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestResponseDto> findAllRequests(@PathVariable Integer userId,
                                                    @PathVariable Integer eventId) {
        User author = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventService.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        return requestService.findAllByRequesorAndEvent(author, event)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public ChangeStateResponseDto changeState(@PathVariable Integer userId,
                                              @PathVariable Integer eventId,
                                              @RequestBody @Valid ChangeStateRequestDto changeStateDto) {
        User author = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        Event event = eventService.findByAuthorAndId(author, eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId + " for user with id - " + userId));

        if (requestService.countAllByIdInAndStateIn(changeStateDto.getRequestIds(), Arrays.asList(Request.State.CANCELED, Request.State.CONFIRMED)) > 0) {
            throw new EventValidationException("Request ids has illegal state");
        }

        ChangeStateRequestDto.Status status = changeStateDto.getStatus();
        Request.State state;
        if (status == ChangeStateRequestDto.Status.CONFIRMED) {
            state = Request.State.CONFIRMED;
        } else if (status == ChangeStateRequestDto.Status.REJECTED) {
            state = Request.State.CANCELED;
        } else {
            throw new IllegalArgumentException("There is no behavior for status " + status);
        }

        List<Request> requests = requestService.changeState(event, changeStateDto.getRequestIds(), state);

        ChangeStateResponseDto changeStateResponseDto = new ChangeStateResponseDto();
        if (status == ChangeStateRequestDto.Status.CONFIRMED) {
            changeStateResponseDto.setConfirmedRequests(requests);
        } else if (status == ChangeStateRequestDto.Status.REJECTED) {
            changeStateResponseDto.setRejectedRequests(requests);
        } else {
            throw new IllegalArgumentException("There is no behavior for status " + status);
        }

        return changeStateResponseDto;
    }

}
