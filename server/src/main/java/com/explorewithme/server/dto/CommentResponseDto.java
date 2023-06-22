package com.explorewithme.server.dto;

import com.explorewithme.server.model.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {

    private Integer id;

    private String text;

    private UserResponseDto author;

    private EventResponseShortDto event;

    private Comment.State state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
