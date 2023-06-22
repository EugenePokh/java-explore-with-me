package com.explorewithme.server.dto;

import com.explorewithme.server.validation.group.AdvancedCheck;
import com.explorewithme.server.validation.group.CommonCheck;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CommentRequestDto {

    @NotNull(groups = {AdvancedCheck.class})
    private Integer eventId;

    @NotBlank(groups = {CommonCheck.class})
    @Size(max = 255, groups = {CommonCheck.class})
    private String text;

    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_COMMENT, REJECT_COMMENT
    }

}
