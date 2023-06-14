package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
