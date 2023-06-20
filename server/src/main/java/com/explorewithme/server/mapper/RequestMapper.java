package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.RequestResponseDto;
import com.explorewithme.server.model.Request;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMapper {

    public RequestResponseDto toDto(Request request) {
        return RequestResponseDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .status(request.getState())
                .build();
    }
}
