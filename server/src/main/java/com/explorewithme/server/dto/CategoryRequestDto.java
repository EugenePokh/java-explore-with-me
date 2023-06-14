package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class CategoryRequestDto {

    @NotBlank
    @Max(value = 50)
    @Min(value = 1)
    private String name;
}
