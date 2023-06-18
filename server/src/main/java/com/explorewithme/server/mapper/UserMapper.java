package com.explorewithme.server.mapper;

import com.explorewithme.server.dto.UserResponseDto;
import com.explorewithme.server.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
