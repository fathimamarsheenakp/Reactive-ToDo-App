package com.sony.todoapp.controller;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/add")
    public Mono<TodoResponseDto> addTask(@Valid @RequestBody TodoRequestDto dto) {
        return todoService.addTask(dto);
    }

    @PostMapping("/add/bulk")
    public Flux<TodoResponseDto> addMultipleTask(@Valid @RequestBody Flux<TodoRequestDto> dtoFlux) {
        return todoService.addMultipleTasks(dtoFlux);
    }

    @GetMapping
    public Flux<TodoResponseDto> getAll() {
        return todoService.getAllTasks();
    }

// Search tasks by name (e.g. /todo/search?name=read)
    @GetMapping("/search")
    public Flux<TodoResponseDto> SearchTask(@RequestParam String name) {
        return todoService.searchTasks(name);
    }

    @PutMapping("/edit/{id}")
    public Mono<ResponseEntity<TodoResponseDto>> updateTask(@PathVariable String id, @Valid @RequestBody TodoRequestDto dto) {
        return todoService.updateTask(id, dto)
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
    public Mono<TodoResponseDto> markCompleted(@PathVariable String id) {
        return todoService.markCompleted(id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteTask(@PathVariable String id) {
        System.out.println("Received delete request for ID: " + id); // debug log
        return todoService.deleteTask(id);
    }
//    Get completed tasks
        @GetMapping("/completed")
        public Mono<ResponseEntity<List<TodoResponseDto>>> getCompletedTasks() {
            return todoService.getCompletedTasks()
                    .collectList()
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Collections.emptyList())));
        }


    //    Get pending tasks
    @GetMapping("/pending")
    public Mono<ResponseEntity<List<TodoResponseDto>>> getPendingTasks() {
        return todoService.getPendingTasks()
                .collectList()
                .map(ResponseEntity::ok) // ✅ returns 200 with list (even if empty)
                .onErrorResume(e -> Mono.just(ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.emptyList()))); // fallback in case of error
    }
}
