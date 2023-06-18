package com.explorewithme.server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompilationResponseDto {

    private Integer id;

    private String title;

    private Boolean pinned;

    private List<EventResponseShortDto> events;
}
