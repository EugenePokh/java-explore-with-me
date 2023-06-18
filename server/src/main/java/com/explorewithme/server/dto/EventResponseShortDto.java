package com.explorewithme.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class EventResponseShortDto {

    private String annotation;

    private CategoryResponseDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer id;

    private UserEventResponseDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

}
