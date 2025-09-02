package com.sony.todoapp.controller;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.User;
import com.sony.todoapp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // Add single task
    @PostMapping("/add")
    public Mono<TodoResponseDto> addTask(@Valid @RequestBody TodoRequestDto dto,
                                         @AuthenticationPrincipal User user) {
        return todoService.addTask(dto, user.getId());
    }

    @PostMapping("/add/bulk")
    public Flux<TodoResponseDto> addMultipleTask(@Valid @RequestBody Flux<TodoRequestDto> dtoFlux,
                                                 @AuthenticationPrincipal User user) {
        return todoService.addMultipleTasks(dtoFlux, user.getId());
    }

    @GetMapping
    public Flux<TodoResponseDto> getAll(@AuthenticationPrincipal User user) {
        return todoService.getAllTasks(user.getId());
    }

    // Search tasks by name (e.g. /todo/search?name=read)
    @GetMapping("/search")
    public Flux<TodoResponseDto> searchTask(@RequestParam String name,
                                            @AuthenticationPrincipal User user) {
        return todoService.searchTasks(name, user.getId());
    }

    @PutMapping("/edit/{id}")
    public Mono<ResponseEntity<TodoResponseDto>> updateTask(@PathVariable String id,
                                                            @Valid @RequestBody TodoRequestDto dto,
                                                            @AuthenticationPrincipal User user) {
        return todoService.updateTask(id, dto, user.getId())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException || e instanceof RuntimeException) {
                        return Mono.just(ResponseEntity.badRequest().body(null));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }


    //    Mark a task as completed
    @PatchMapping("/{id}/complete")
    public Mono<TodoResponseDto> markCompleted(@PathVariable String id,
                                               @AuthenticationPrincipal User user) {
        return todoService.markCompleted(id, user.getId());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteTask(@PathVariable String id,
                                 @AuthenticationPrincipal User user) {
        return todoService.deleteTask(id, user.getId());
    }

//    Get completed tasks
    @GetMapping("/completed")
    public Mono<ResponseEntity<List<TodoResponseDto>>> getCompletedTasks(@AuthenticationPrincipal User user) {
        return todoService.getCompletedTasks(user.getId())
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.emptyList())));
    }


    //    Get pending tasks
    @GetMapping("/pending")
    public Mono<ResponseEntity<List<TodoResponseDto>>> getPendingTasks(@AuthenticationPrincipal User user) {
        return todoService.getPendingTasks(user.getId())
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.emptyList())));
    }
}
