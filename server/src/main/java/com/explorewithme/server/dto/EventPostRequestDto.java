package com.explorewithme.server.dto;

import com.explorewithme.server.annotation.EventDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventPostRequestDto {

    @NotBlank
    @Max(value = 2000)
    @Min(value = 20)
    private String annotation;

    @NotNull
    private Integer category;

    @NotBlank
    @Max(value = 7000)
    @Min(value = 20)
    private String description;

    @EventDateConstraint
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Max(value = 120)
    @Min(value = 3)
    private String title;
}
