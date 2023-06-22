package com.explorewithme.server.controller;

import com.explorewithme.server.dto.CommentRequestDto;
import com.explorewithme.server.dto.CommentResponseDto;
import com.explorewithme.server.service.CommentService;
import com.explorewithme.server.validation.group.AdvancedCheck;
import com.explorewithme.server.validation.group.CommonCheck;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> post(@PathVariable Integer userId,
                                                   @RequestBody @Validated({CommonCheck.class, AdvancedCheck.class}) CommentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.post(userId, dto));
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto patch(@PathVariable Integer userId,
                                    @PathVariable Integer commentId,
                                    @RequestBody @Validated({CommonCheck.class}) CommentRequestDto dto) {
        return commentService.patch(userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer userId,
                                       @PathVariable Integer commentId) {
        commentService.delete(userId, commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
