package com.explorewithme.server.controller;

import com.explorewithme.server.dto.RequestResponseDto;
import com.explorewithme.server.service.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestResponseDto> findAll(@PathVariable Integer userId) {

        return requestService.findAllByRequester(userId);
    }

    @PostMapping
    public ResponseEntity<RequestResponseDto> post(@PathVariable Integer userId,
                                                   @RequestParam Integer eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.post(userId, eventId));
    }

    @PatchMapping("{requestId}/cancel")
    public RequestResponseDto cancelRequest(@PathVariable Integer userId,
                                            @PathVariable Integer requestId) {
        return requestService.cancel(userId, requestId);
    }

}
