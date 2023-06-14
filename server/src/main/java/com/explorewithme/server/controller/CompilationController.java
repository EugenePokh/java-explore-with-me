package com.explorewithme.server.controller;

import com.explorewithme.server.exception.CompilationNotFoundException;
import com.explorewithme.server.model.Compilation;
import com.explorewithme.server.service.CompilationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<Compilation> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                     @RequestParam(required = false, defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Boolean pinned) {
        PageRequest page = PageRequest.of(from / size, size);

        return compilationService.findAllByPinned(pinned, page)
                .getContent();
    }

    @GetMapping("/{compilationId}")
    public Compilation findById(@PathVariable Integer compilationId) {
        return compilationService.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("No such category with id - " + compilationId));
    }

}
