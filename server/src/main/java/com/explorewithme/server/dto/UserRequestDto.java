package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class UserRequestDto {

    @NotBlank
    @Max(value = 250)
    @Min(value = 2)
    private String name;

    @NotBlank
    @Email
    @Max(value = 254)
    @Min(value = 6)
    private String email;
}
