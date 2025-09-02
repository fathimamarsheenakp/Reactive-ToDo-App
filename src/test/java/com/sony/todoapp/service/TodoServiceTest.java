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
    private TodoService todoService; // <-- only the service, no Spring context

    private Todo todo;
    private TodoRequestDto requestDto;
    private final String dummyUserId = "dummyUserId";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // initialize mocks

        requestDto = new TodoRequestDto();
        requestDto.setName("Task Name");
        requestDto.setDescription("Task Description");

        todo = new Todo();
        todo.setId("1");
        todo.setName("Task Name");
        todo.setDescription("Task Description");
        todo.setUserId(dummyUserId);
    }

    @Test
    public void testAddTask() {
        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.addTask(requestDto, dummyUserId))
                .expectNextMatches(t ->
                        t.getName().equals("Task Name"))
                .verifyComplete();
    }

    @Test
    public void testAddMultipleTasks() {
        TodoRequestDto dto2 = new TodoRequestDto();
        dto2.setName("Task 2");
        dto2.setDescription("Description 2");

        Todo todo2 = new Todo();
        todo2.setId("2");
        todo2.setName("Task 2");
        todo2.setDescription("Description 2");
        todo2.setUserId(dummyUserId);

        when(repository.saveAll(anyList())).thenReturn(Flux.just(todo, todo2));

        StepVerifier.create(todoService.addMultipleTasks(Flux.just(requestDto, dto2), dummyUserId))
                .expectNextMatches(dto -> dto.getName().equals("Task Name"))
                .expectNextMatches(dto -> dto.getName().equals("Task 2"))
                .verifyComplete();
    }

    @Test
    public void testGetAllTasks() {
        when(repository.findByUserId(dummyUserId)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getAllTasks(dummyUserId))
                .expectNextMatches(dto ->
                        dto.getName().equals("Task Name"))
                .verifyComplete();
    }

    @Test
    public void testUpdateTask_success() {
        when(repository.findByIdAndUserId("1", dummyUserId)).thenReturn(Mono.just(todo));
        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.updateTask("1", requestDto, dummyUserId))
                .expectNextMatches(dto -> dto.getName().equals("Task Name") &&
                        dto.getDescription().equals("Task Description"))
                .verifyComplete();
    }

    @Test
    public void testMarkCompleted() {
        todo.setCompleted(false);

        when(repository.findByIdAndUserId("1", dummyUserId)).thenReturn(Mono.just(todo));
        when(repository.save(any(Todo.class))).thenReturn(Mono.just(todo));

        StepVerifier.create(todoService.markCompleted("1", dummyUserId))
                .expectNextMatches(TodoResponseDto::isCompleted)
                .verifyComplete();
    }

    @Test
    public void testDeleteTask() {
        when(repository.findByIdAndUserId("1", dummyUserId)).thenReturn(Mono.just(todo));
        when(repository.delete(todo)).thenReturn(Mono.empty());

        StepVerifier.create(todoService.deleteTask("1", dummyUserId))
                .verifyComplete();
    }

    @Test
    public void testGetCompletedTasks() {
        todo.setCompleted(true);
        when(repository.findByCompletedAndUserId(true, dummyUserId)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getCompletedTasks(dummyUserId))
                .expectNextMatches(TodoResponseDto::isCompleted)
                .verifyComplete();
    }

    @Test
    public void testGetPendingTasks() {
        todo.setCompleted(false);
        when(repository.findByCompletedAndUserId(false, dummyUserId)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.getPendingTasks(dummyUserId))
                .expectNextMatches(dto -> !dto.isCompleted())
                .verifyComplete();
    }

    @Test
    public void testSearchTasks() {
        when(repository.findByNameContainingIgnoreCase("Task", dummyUserId)).thenReturn(Flux.just(todo));

        StepVerifier.create(todoService.searchTasks("Task", dummyUserId))
                .expectNextMatches(dto -> dto.getName().equals("Task Name"))
                .verifyComplete();
    }
}
