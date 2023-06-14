package com.explorewithme.server.service;

import com.explorewithme.server.model.Category;
import com.explorewithme.server.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryRepository categoryRepository;

    public void delete(Category category) {
        logger.info("Delete category - " + category);
        categoryRepository.delete(category);
    }

    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        logger.info("Save category - " + category);
        return categoryRepository.save(category);
    }

    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
