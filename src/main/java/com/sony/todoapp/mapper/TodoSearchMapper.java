package com.sony.todoapp.mapper;

import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;
import com.sony.todoapp.entity.TodoEs;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TodoSearchMapper {

    // Entity → Elasticsearch entity
    TodoEs toEs(Todo entity);

    // Elasticsearch entity → Response DTO
    TodoResponseDto toDto(TodoEs entity);
}


