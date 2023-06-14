package com.explorewithme.server.controller;

import com.explorewithme.server.exception.CategoryNotFoundException;
import com.explorewithme.server.model.Category;
import com.explorewithme.server.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoryService.findAll(page)
                .getContent();
    }

    @GetMapping("/categories/{categoryId}")
    public Category findById(@PathVariable Integer categoryId) {
        return categoryService.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + categoryId));
    }
}
