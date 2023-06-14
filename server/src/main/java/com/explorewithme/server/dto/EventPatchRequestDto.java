package com.explorewithme.server.dto;

import com.explorewithme.server.annotation.EventDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventPatchRequestDto {

    private String annotation;

    private Integer category;

    private String description;

    @EventDateConstraint
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String title;

    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT, SEND_TO_REVIEW, CANCEL_REVIEW
    }
}
