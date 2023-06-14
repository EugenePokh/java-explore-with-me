package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryRequestDto {

    @NotBlank
    private String name;
}
