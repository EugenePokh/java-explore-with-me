package com.explorewithme.server.controller;

import com.explorewithme.server.annotation.LogExecutionTime;
import com.explorewithme.server.dto.*;
import com.explorewithme.server.exception.CategoryNotFoundException;
import com.explorewithme.server.exception.CompilationNotFoundException;
import com.explorewithme.server.exception.EventNotFoundException;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.EventMapper;
import com.explorewithme.server.model.*;
import com.explorewithme.server.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;
    private final EventCompilationService eventCompilationService;

    @PostMapping("/categories")
    @LogExecutionTime
    public ResponseEntity<Category> postCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());

        Category newCategory = categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newCategory);
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + categoryId));
        categoryService.delete(category);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/categories/{categoryId}")
    public Category patchCategory(@PathVariable Integer categoryId,
                                  @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + categoryId));
        category.setName(categoryRequestDto.getName());

        return categoryService.save(category);
    }

    @GetMapping("/users")
    public List<User> findAllUsers(@RequestParam(required = false) List<Integer> ids,
                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("created").descending());
        Page<User> users;
        if (Objects.nonNull(ids) && !ids.isEmpty()) {
            users = userService.findAllByIds(ids, page);
        } else {
            users = userService.findAll(page);
        }

        return users.getContent();
    }

    @PostMapping("/users")
    public User postUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setName(userRequestDto.getName());
        user.setCreated(LocalDateTime.now());

        return userService.save(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));
        userService.delete(user);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/events")
    public List<EventResponseDto> findAllEvents(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                @RequestParam(required = false) List<Integer> users,
                                                @RequestParam(required = false) List<Event.State> states,
                                                @RequestParam(required = false) List<Integer> categories,
                                                @RequestParam(required = false) LocalDateTime rangeStat,
                                                @RequestParam(required = false) LocalDateTime rangeEnd) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventService.findAllBySearch(users, states, categories, rangeStat, rangeEnd, page)
                .getContent()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/events/{eventId}")
    public EventResponseDto patchEvent(@PathVariable Integer eventId,
                                       @RequestBody @Valid EventPatchRequestDto eventRequestDto) {
        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event with id - " + eventId));

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
            } else if (eventRequestDto.getStateAction() == EventPatchRequestDto.StateAction.REJECT_EVENT) {
                state = Event.State.CANCELED;
            } else {
                throw new IllegalArgumentException("Define behavior for status " + eventRequestDto.getStateAction());
            }
            event.setState(state);
        }

        return EventMapper.toDto(eventService.save(event));
    }

    @PostMapping("/compilations")
    public Compilation postCompilation(@RequestBody @Valid CompilationRequestDto dto) {
        return compilationService.create(dto);
    }

    @DeleteMapping("/compilations/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Integer compilationId) {
        Compilation compilation = compilationService.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId));
        compilationService.delete(compilation);

        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/compilations/{compilationId}")
    public Compilation patchCompilation(@PathVariable Integer compilationId,
                                        @RequestBody @Valid CompilationRequestDto dto) {
        Compilation compilation = compilationService.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId));

        if (Objects.nonNull(dto.getEvents())) {
            List<EventCompilation> eventCompilations;
            if (dto.getEvents().isEmpty()) {
                eventCompilations = new ArrayList<>();
            } else {
                eventCompilations = eventCompilationService.findAllByCompilationAndEventIdIn(compilation, dto.getEvents());
            }
            compilation.setEventCompilations(eventCompilations);
        }

        if (Objects.nonNull(dto.getTitle())) {
            compilation.setTitle(dto.getTitle());
        }

        if (Objects.nonNull(dto.getPinned())) {
            compilation.setPinned(dto.getPinned());
        }

        return compilationService.save(compilation);
    }
}
