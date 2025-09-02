package com.sony.todoapp.mapper;


import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;

public class TodoMapper {

    public static Todo toEntity(TodoRequestDto dto) {

        if (dto == null) {
            return null;
        }

        Todo todo = new Todo();
        todo.setName(dto.getName());
        todo.setDescription(dto.getDescription());
        todo.setUserId(dto.getUserId());

        return todo;
    }

    public static TodoResponseDto toDto(Todo entity) {

        if (entity == null)
            return null;

        TodoResponseDto responseDto = new TodoResponseDto();
        responseDto.setId(entity.getId());
        responseDto.setName(entity.getName());
        responseDto.setDescription(entity.getDescription());
        responseDto.setCompleted(entity.isCompleted());
        responseDto.setCreatedAt(entity.getCreatedAt());
        responseDto.setUserId(entity.getUserId());
        return responseDto;
    }
}
