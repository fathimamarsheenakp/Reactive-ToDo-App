package com.sony.todoapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "todos")
public class Todo {

    @Id
    private String id;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String userId;
}
