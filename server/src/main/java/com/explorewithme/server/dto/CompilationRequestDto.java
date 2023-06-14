package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Data
public class CompilationRequestDto {

    private List<Integer> events;
    private Boolean pinned = false;

    @Max(value = 50)
    @Min(value = 1)
    private String title;

}
