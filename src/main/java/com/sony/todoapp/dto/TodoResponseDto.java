package com.sony.todoapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoResponseDto {

    private String id;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
}
