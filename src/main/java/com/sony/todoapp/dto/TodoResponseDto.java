package com.sony.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponseDto {

    private String id;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private String userId;
}
