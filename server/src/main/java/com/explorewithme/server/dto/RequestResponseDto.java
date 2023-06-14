package com.explorewithme.server.dto;

import com.explorewithme.server.model.Request;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestResponseDto {
    private LocalDateTime created;
    private Integer event;
    private Integer id;
    private Integer requester;
    private Request.State status;
}
