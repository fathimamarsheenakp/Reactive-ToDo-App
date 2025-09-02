package com.sony.todoapp.mapper;

import com.sony.todoapp.dto.UserRequestDto;
import com.sony.todoapp.dto.UserResponseDto;
import com.sony.todoapp.entity.User;

public class UserMapper {

    // Convert DTO → Entity (when registering a user)
    public static User toEntity(UserRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // ⚠️ hash it later in service!
        return user;
    }

    // Convert Entity → DTO (when sending response to client)
    public static UserResponseDto toDto(User user, String token) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(user.getUsername());
        dto.setToken(token);
        return dto;
    }
}

