package com.explorewithme.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChangeStateResponseDto {

    private List<RequestResponseDto> confirmedRequests;
    private List<RequestResponseDto> rejectedRequests;
}
