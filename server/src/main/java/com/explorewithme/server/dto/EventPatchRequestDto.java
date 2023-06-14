package com.explorewithme.server.dto;

import com.explorewithme.server.annotation.EventDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
public class EventPatchRequestDto {

    @Max(value = 2000)
    @Min(value = 20)
    private String annotation;

    private Integer category;

    @Max(value = 7000)
    @Min(value = 20)
    private String description;

    @EventDateConstraint
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Max(value = 120)
    @Min(value = 3)
    private String title;

    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT, SEND_TO_REVIEW, CANCEL_REVIEW
    }
}
