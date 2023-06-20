package com.explorewithme.server.dto;

import com.explorewithme.server.validation.annotation.CommonCheck;
import com.explorewithme.server.validation.annotation.AdvancedCheck;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CompilationRequestDto {

    private List<Integer> events;
    private Boolean pinned = false;

    @Size(min = 1, max = 50, groups = {CommonCheck.class})
    @NotBlank(groups = {AdvancedCheck.class})
    private String title;

}
