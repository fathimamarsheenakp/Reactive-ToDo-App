package com.sony.todoapp.service;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;
import com.sony.todoapp.exception.ResourceNotFoundException;
import com.sony.todoapp.exception.TaskAlreadyCompletedException;
import com.sony.todoapp.mapper.TodoMapper;
import com.sony.todoapp.mapper.TodoSearchMapper;
import com.sony.todoapp.repository.TodoEsRepository;
import com.sony.todoapp.repository.TodoRepository;
import com.sony.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository repository;
    private final UserRepository userRepository;
    private final TodoMapper mapper;
    private final TodoSearchMapper searchMapper;
    private final TodoEsRepository esRepository;

    //    Add Single task
    public Mono<TodoResponseDto> addTask(TodoRequestDto dto, String userId) {
        Todo entity = mapper.toEntity(dto);
        entity.setUserId(userId);

        return repository.save(entity)
                .flatMap(saved ->
                        esRepository.save(searchMapper.toEs(saved))
                                .thenReturn(saved))
                .map(mapper::toDto);
    }


    //    Add multiple tasks
    public Flux<TodoResponseDto> addMultipleTasks(Flux<TodoRequestDto> dtoFlux, String userId) {
        return dtoFlux
                .map(mapper::toEntity)
                .map(entity -> {
                    entity.setUserId(userId);
                    return entity;
                })
                .collectList()
                .flatMapMany(repository::saveAll)
                .flatMap(saved ->
                        esRepository.save(searchMapper.toEs(saved))  // save each TodoEs
                                .then(Mono.just(saved))              // return the original Todo
                )
                .map(mapper::toDto);
    }



    //    Get All tasks
    public Flux<TodoResponseDto> getAllTasks(String userId) {
        return esRepository.searchByUserId(userId) // returns Flux<TodoEs>
                .switchIfEmpty(Flux.error(new ResourceNotFoundException("No tasks found for user " + userId)))
                .map(searchMapper::toDto);         // map each TodoEs -> DTO
    }

    //    Search tasks by name
    public Flux<TodoResponseDto> searchTasks(String name, String userId) {
        return esRepository.searchByNameContainingIgnoreCaseAndUserId(name, userId)
                .switchIfEmpty(Flux.error(new ResourceNotFoundException("No tasks found")))
                .map(searchMapper::toDto);
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
                })
                .flatMap(saved ->
                        esRepository.save(searchMapper.toEs(saved))
                                .thenReturn(saved))
                .map(mapper::toDto);
    }


    //    Mark task as completed
    public Mono<TodoResponseDto> markCompleted(String id, String userId) {
        return repository.findByIdAndUserId(id, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found")))
                .flatMap(todo -> {
                    todo.setCompleted(true);
                    return repository.save(todo);
                })
                .flatMap(saved ->
                        esRepository.save(searchMapper.toEs(saved))
                                .thenReturn(saved))
                .map(mapper::toDto);
    }


    //    Delete a task
    public Mono<Void> deleteTask(String id, String userId) {
        return repository.findByIdAndUserId(id, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found or not yours")))
                .flatMap(todo -> repository.delete(todo)
                        .then(esRepository.deleteById(todo.getId()))
                );
    }


    //  Get completed tasks
    public Flux<TodoResponseDto> getCompletedTasksEs(String userId) {
        return esRepository.findByCompletedAndUserId(true, userId)
                .map(searchMapper::toDto);
    }

    //  Get pending tasks
    public Flux<TodoResponseDto> getPendingTasksEs(String userId) {
        return esRepository.findByCompletedAndUserId(false, userId)
                .map(searchMapper::toDto);
    }
}
