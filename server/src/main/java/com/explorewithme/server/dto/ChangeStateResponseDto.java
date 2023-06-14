package com.explorewithme.server.dto;

import com.explorewithme.server.model.Request;
import lombok.Data;

import java.util.List;

@Data
public class ChangeStateResponseDto {

    private List<Request> confirmedRequests;
    private List<Request> rejectedRequests;
}
