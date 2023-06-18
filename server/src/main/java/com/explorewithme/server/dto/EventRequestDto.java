package com.explorewithme.server.dto;

import com.explorewithme.server.validation.annotation.EventDateConstraint;
import com.explorewithme.server.validation.annotation.CommonCheck;
import com.explorewithme.server.validation.annotation.AdvancedCheck;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventRequestDto {

    @NotBlank(groups = {AdvancedCheck.class})
    @Size(min = 20, max = 2000, groups = {CommonCheck.class})
    private String annotation;

    @NotNull(groups = {AdvancedCheck.class})
    private Integer category;

    @NotBlank(groups = {AdvancedCheck.class})
    @Size(min = 20, max = 7000, groups = {CommonCheck.class})
    private String description;

    @EventDateConstraint(groups = {CommonCheck.class})
    @NotNull(groups = {AdvancedCheck.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(groups = {AdvancedCheck.class})
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(groups = {CommonCheck.class})
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(groups = {AdvancedCheck.class})
    @Size(min = 3, max = 120, groups = {CommonCheck.class})
    private String title;

    private StateAction stateAction;

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT
    }

}
