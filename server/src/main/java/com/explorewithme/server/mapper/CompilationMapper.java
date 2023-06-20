package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.CompilationResponseDto;
import com.explorewithme.server.model.Compilation;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public CompilationResponseDto toDto(Compilation compilation) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(Objects.isNull(compilation.getEvents()) ?
                        null : compilation.getEvents()
                        .stream()
                        .map(EventMapper::toShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
