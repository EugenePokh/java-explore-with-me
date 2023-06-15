package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CompilationPatchRequestDto {

    private List<Integer> events;
    private Boolean pinned = false;

    @Size(min = 1, max = 50)
    private String title;

}
