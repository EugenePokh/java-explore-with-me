package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRequestDto {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
