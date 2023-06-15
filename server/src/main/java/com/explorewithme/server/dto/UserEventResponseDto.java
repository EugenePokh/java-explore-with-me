package com.explorewithme.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEventResponseDto {

    private Integer id;
    private String name;
}
