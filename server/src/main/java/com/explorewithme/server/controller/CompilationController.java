package com.explorewithme.server.controller;

import com.explorewithme.server.dto.CompilationResponseDto;
import com.explorewithme.server.service.CompilationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationResponseDto> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                                @RequestParam(required = false) Boolean pinned) {
        return compilationService.findAllByPinned(pinned, from, size)
                .getContent();
    }

    @GetMapping("/{compilationId}")
    public CompilationResponseDto findById(@PathVariable Integer compilationId) {
        return compilationService.findById(compilationId);
    }

}
