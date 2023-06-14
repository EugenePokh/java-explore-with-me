package com.explorewithme.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class HitRequestDto {
    @Size(max = 30)
    @NotBlank
    private String app;

    @NotBlank
    @Size(max = 150)
    private String uri;

    @NotBlank
    @Size(max = 30)
    private String ip;

    private LocalDateTime timestamp;
}
