package com.explorewithme.server.dto;

import com.explorewithme.server.annotation.EventDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventPostRequestDto {

    @NotBlank
    private String annotation;

    @NotNull
    private Integer category;

    @NotBlank
    private String description;

    @EventDateConstraint
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    @Min(1)
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank
    private String title;
}
