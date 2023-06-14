package com.explorewithme.server.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChangeStateRequestDto {

    @NotNull
    @NotEmpty
    private List<Integer> requestIds;

    @NotNull
    private Status status;

    public enum Status {

        CONFIRMED, REJECTED

    }
}
