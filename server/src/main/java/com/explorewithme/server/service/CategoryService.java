package com.explorewithme.server.service;

import com.explorewithme.server.dto.CategoryRequestDto;
import com.explorewithme.server.dto.CategoryResponseDto;
import com.explorewithme.server.exception.CategoryNotFoundException;
import com.explorewithme.server.mapper.CategoryMapper;
import com.explorewithme.server.model.Category;
import com.explorewithme.server.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryResponseDto findById(Integer id) {
        return CategoryMapper.toDto(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + id)));
    }

    @Transactional
    public CategoryResponseDto post(CategoryRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());

        Category created = categoryRepository.save(category);
        logger.info("Save category - " + created);

        return CategoryMapper.toDto(created);
    }

    @Transactional
    public void deleteById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + categoryId));
        logger.info("Delete category - " + category);
        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponseDto patch(Integer categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No such category with id - " + categoryId));
        category.setName(categoryRequestDto.getName());

        Category updated = categoryRepository.save(category);
        logger.info("Updated category - " + updated);

        return CategoryMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> findAll(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper::toDto);
    }
}
