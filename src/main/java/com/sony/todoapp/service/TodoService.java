package com.sony.todoapp.service;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;
import com.sony.todoapp.exception.ResourceNotFoundException;
import com.sony.todoapp.exception.TaskAlreadyCompletedException;
import com.sony.todoapp.mapper.TodoMapper;
import com.sony.todoapp.repository.TodoRepository;
import com.sony.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repository;
    private final UserRepository userRepository;

//    Add Single task
    public Mono<TodoResponseDto> addTask(TodoRequestDto dto, String userId) {
        Todo entity = TodoMapper.toEntity(dto);
        entity.setUserId(userId);
        return repository.save(entity)
                .map(TodoMapper::toDto);
    }


    //    Add multiple tasks
    public Flux<TodoResponseDto> addMultipleTasks(Flux<TodoRequestDto> dtoFlux, String userId) {
        return dtoFlux
                .map(TodoMapper::toEntity)
                .map(entity -> {
                    entity.setUserId(userId);
                    return entity;
                })
                .collectList()
                .flatMapMany(repository::saveAll)
                .map(TodoMapper::toDto);
    }



    //    Get All tasks
    public Flux<TodoResponseDto> getAllTasks(String userId) {
        return repository.findByUserId(userId)
                .map(TodoMapper::toDto);
    }

//    Search tasks by name
    public Flux<TodoResponseDto> searchTasks(String name, String userId) {
        return repository.findByNameContainingIgnoreCase(name, userId)
                .map(TodoMapper::toDto);
    }


    //    Update tasks
    public Mono<TodoResponseDto> updateTask(String id, TodoRequestDto dto, String userId) {
        return repository.findByIdAndUserId(id, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                .flatMap(existing -> {
                    if (existing.isCompleted()) {
                        return Mono.error(new TaskAlreadyCompletedException("Cannot update a completed task"));
                    }
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    return repository.save(existing);
                }).map(TodoMapper::toDto);
    }


    //    Mark task as completed
        public Mono<TodoResponseDto> markCompleted(String id, String userId) {
            return repository.findByIdAndUserId(id, userId)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found or not yours")))
                    .flatMap(todo -> {
                        todo.setCompleted(true);
                        return repository.save(todo);
                    })
                    .map(TodoMapper::toDto);
        }


        //    Delete a task
        public Mono<Void> deleteTask(String id, String userId) {
            return repository.findByIdAndUserId(id, userId)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found or not yours")))
                    .flatMap(repository::delete);
        }


        //    Get completed tasks
        public Flux<TodoResponseDto> getCompletedTasks(String userId) {
            return repository.findByCompletedAndUserId(true, userId)
                    .map(TodoMapper::toDto);
        }

        public Flux<TodoResponseDto> getPendingTasks(String userId) {
            return repository.findByCompletedAndUserId(false, userId)
                    .map(TodoMapper::toDto);
        }



}
