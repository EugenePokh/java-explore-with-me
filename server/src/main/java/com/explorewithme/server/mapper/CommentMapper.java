package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.CommentResponseDto;
import com.explorewithme.server.model.Comment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {

    public CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toDto(comment.getUser()))
                .event(EventMapper.toShortDto(comment.getEvent()))
                .state(comment.getState())
                .created(comment.getCreated())
                .build();
    }
}
