package com.explorewithme.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompilationRequestDto {

    private List<Integer> events;
    private Boolean pinned;
    private String title;

}
