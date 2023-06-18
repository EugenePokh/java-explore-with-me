package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.CategoryResponseDto;
import com.explorewithme.server.model.Category;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {

    public CategoryResponseDto toDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
