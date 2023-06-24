package com.explorewithme.server.controller;

import com.explorewithme.server.dto.*;
import com.explorewithme.server.model.*;
import com.explorewithme.server.service.*;
import com.explorewithme.server.validation.group.CommonCheck;
import com.explorewithme.server.validation.group.AdvancedCheck;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;
    private final CommentService commentService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> postCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.post(categoryRequestDto));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteById(categoryId);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/categories/{categoryId}")
    public CategoryResponseDto patchCategory(@PathVariable Integer categoryId,
                                             @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return categoryService.patch(categoryId, categoryRequestDto);
    }

    @GetMapping("/users")
    public List<UserResponseDto> findAllUsers(@RequestParam(required = false) List<Integer> ids,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        return userService.findAllByIds(ids, from, size)
                .getContent();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> postUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.post(userRequestDto));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/events")
    public List<EventResponseFullDto> findAllEvents(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) List<Integer> users,
                                                    @RequestParam(required = false) List<Event.State> states,
                                                    @RequestParam(required = false) List<Integer> categories,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd) {
        return eventService.findAllBySearch(users, states, categories, rangeStart, rangeEnd, from, size)
                .getContent();
    }

    @PatchMapping("/events/{eventId}")
    public EventResponseFullDto patchEvent(@PathVariable Integer eventId,
                                           @RequestBody @Validated({CommonCheck.class}) EventRequestDto eventRequestDto) {
        return eventService.patch(eventId, eventRequestDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentResponseDto patchComment(@PathVariable Integer commentId,
                                           @RequestBody @Validated({CommonCheck.class}) CommentRequestDto commentRequestDto) {
        return commentService.patch(commentId, commentRequestDto);
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationResponseDto> postCompilation(@RequestBody @Validated({AdvancedCheck.class, CommonCheck.class}) CompilationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compilationService.post(dto));
    }

    @DeleteMapping("/compilations/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Integer compilationId) {
        compilationService.deleteById(compilationId);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/compilations/{compilationId}")
    public CompilationResponseDto patchCompilation(@PathVariable Integer compilationId,
                                                   @RequestBody @Validated({CommonCheck.class}) CompilationRequestDto dto) {
        return compilationService.patch(compilationId, dto);
    }
}
