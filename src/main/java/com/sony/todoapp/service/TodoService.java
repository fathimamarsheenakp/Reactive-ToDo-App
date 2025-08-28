package com.sony.todoapp.service;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;
import com.sony.todoapp.mapper.TodoMapper;
import com.sony.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repository;

//    Add Single task
    public Mono<TodoResponseDto> addTask(TodoRequestDto dto) {
        Todo entity = TodoMapper.toEntity(dto);
        return repository.save(entity)
                .map(TodoMapper::toDto);
    }

//    Add multiple tasks
    public Flux<TodoResponseDto> addMultipleTasks(Flux<TodoRequestDto> dtoFlux) {
        return dtoFlux
                .map(TodoMapper::toEntity)
                .collectList()
                .flatMapMany(repository::saveAll)
                .map(TodoMapper::toDto);
    }


//    Get All tasks
    public Flux<TodoResponseDto> getAllTasks() {
        return repository.findAll()
                .map(TodoMapper::toDto);
    }

//    Search tasks by name
    public Flux<TodoResponseDto> searchTasks(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .map(TodoMapper::toDto)
                .switchIfEmpty(Flux.empty());
    }

//    Update tasks
    public Mono<TodoResponseDto> updateTask(String id, TodoRequestDto dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Task not found")))
                .flatMap(existing -> {
                    if (existing.isCompleted()) {
                        return Mono.error(new IllegalStateException("Cannot update a completed task"));
                    }
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    return repository.save(existing);
                }).map(TodoMapper::toDto);
    }

//    Mark task as completed
    public Mono<TodoResponseDto> markCompleted(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Task not found")))
                .flatMap(todo -> {
                    todo.setCompleted(true);
                    return repository.save(todo);
                })
                .map(TodoMapper::toDto);
    }

//    Delete a task
    public Mono<Void> deleteTask(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Task not found")))
                .flatMap(repository::delete);
    }

//    Get completed tasks
    public Flux<TodoResponseDto> getCompletedTasks() {
        return repository.findByCompleted(true)
                .map(TodoMapper::toDto);
    }



    //    Get pending tasks
    public Flux<TodoResponseDto> getPendingTasks() {
        return repository.findByCompleted(false)
                .map(TodoMapper::toDto);
    }


}
