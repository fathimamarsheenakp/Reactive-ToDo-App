package com.sony.todoapp.service;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.Todo;
import com.sony.todoapp.mapper.TodoMapper;
import com.sony.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class TodoServiceTest {

    @Mock
    private TodoRepository repository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo;
    private TodoRequestDto requestDto;
    private TodoResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new TodoRequestDto();
        requestDto.setName("Task Name");
        requestDto.setDescription("Task Description");

        todo = new Todo();
        todo.setId("1");
        todo.setName("Test Task");
        todo.setDescription("Test Description");
        todo.setCompleted(false);

        responseDto = TodoMapper.toDto(todo);
    }

    @Test
    public void testAddTask() {
        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.addTask(requestDto))
                .expectNextMatches(dto -> dto.getName().equals("Test Task"))
                .verifyComplete();
    }

//    public void addMultipleTasks() {
//
//    }

    @Test
    public void testGetAllTasks() {
        when(repository.findAll()).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getAllTasks())
                .expectNext(responseDto)
                .verifyComplete();
    }

    @Test
    public void testSearchTasks_found() {
        when(repository.findByNameContainingIgnoreCase("Test")).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.searchTasks("Test"))
                .expectNext(responseDto)
                .verifyComplete();
    }

    @Test
    public void testSearchTasks_notFound() {
        when(repository.findByNameContainingIgnoreCase("Unknown")).thenReturn(Flux.empty());

        StepVerifier.create(todoService.searchTasks("Unknown"))
                .verifyComplete();
    }

    @Test
    public void testUpdateTask_success() {
        when(repository.findById("1")).thenReturn(Mono.just(todo));

        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.updateTask("1", requestDto))
                .expectNextMatches(dto ->
                        dto.getName().equals("Task Name") &&
                                dto.getDescription().equals("Task Description")
                )
                .verifyComplete();
    }

    @Test
    public void testUpdateTask_notFound() {
        when(repository.findById("99")).thenReturn(Mono.empty());

        StepVerifier.create(todoService.updateTask("99", requestDto))
                .expectErrorMatches(e-> e instanceof RuntimeException && e.getMessage().equals("Task not found"))
                .verify();
    }

    @Test
    public void testMarkCompleted() {
        todo.setCompleted(false);

        when(repository.findById("1")).thenReturn(Mono.just(todo));

        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.markCompleted("1"))
                .expectNextMatches(TodoResponseDto::isCompleted)
                .verifyComplete();
    }

    @Test
    public void testDeleteTask_success() {
        when(repository.findById("1")).thenReturn(Mono.just(todo));

        when(repository.delete(todo)).thenReturn(Mono.empty());

        StepVerifier.create(todoService.deleteTask("1"))
                .verifyComplete();
    }

    @Test
    public void testGetCompletedTasks() {
        todo.setCompleted(true);

        when(repository.findByCompleted(true)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getCompletedTasks())
                .expectNextMatches(TodoResponseDto::isCompleted)
                .verifyComplete();
    }

    @Test
    public void testGetPendingTasks() {
        todo.setCompleted(false);

        when(repository.findByCompleted(false)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getPendingTasks())
                .expectNextMatches(dto -> !dto.isCompleted())
                .verifyComplete();
    }

    @Test
    public void testAddMultipleTasks() {
        TodoRequestDto dto1 = new TodoRequestDto();
        dto1.setName("Task 1");
        dto1.setDescription("Description 1");

        TodoRequestDto dto2 = new TodoRequestDto();
        dto2.setName("Task 2");
        dto2.setDescription("Description 2");

        Todo todo1 = new Todo();
        todo1.setId("1");
        todo1.setName("Task 1");
        todo1.setDescription("Description 1");

        Todo todo2 = new Todo();
        todo2.setId("2");
        todo2.setName("Task 2");
        todo2.setDescription("Description 2");

        when(repository.saveAll(anyList())).thenReturn(Flux.just(todo1, todo2));

        StepVerifier.create(todoService.addMultipleTasks(Flux.just(dto1, dto2)))
                .expectNextMatches(dto -> dto.getName().equals("Task 1"))
                .expectNextMatches(dto -> dto.getName().equals("Task 2"))
                .verifyComplete();
    }
}
