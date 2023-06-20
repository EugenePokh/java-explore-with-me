package com.explorewithme.server.controller;

import com.explorewithme.server.dto.CategoryResponseDto;
import com.explorewithme.server.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryResponseDto> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        return categoryService.findAll(from, size)
                .getContent();
    }

    @GetMapping("/categories/{categoryId}")
    public CategoryResponseDto findById(@PathVariable Integer categoryId) {
        return categoryService.findById(categoryId);
    }
}
