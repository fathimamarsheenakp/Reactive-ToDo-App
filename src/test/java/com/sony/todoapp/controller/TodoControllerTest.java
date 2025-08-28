package com.sony.todoapp.controller;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TodoControllerTest {
    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private WebTestClient webTestClient;

    private TodoRequestDto requestDto;
    private TodoResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(todoController).build();

        requestDto = new TodoRequestDto();
        requestDto.setName("Test Task");
        requestDto.setDescription("Test Description");

        responseDto = new TodoResponseDto();
        responseDto.setId("1");
        responseDto.setName("Test Task");
        responseDto.setDescription("Test Description");
        responseDto.setCompleted(false);
    }

    @Test
    public void testAddTask() {
        when(todoService.addTask(any(TodoRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/todo/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseDto.class)
                .isEqualTo(responseDto);
    }

    @Test
    public void testAddMultipleTasks() {
        when(todoService.addMultipleTasks(any(Flux.class))).thenReturn(Flux.just(responseDto));

        webTestClient.post()
                .uri("/todo/add/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(requestDto), TodoRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .contains(responseDto);
    }

    @Test
    public void testGetAllTasks() {
        when(todoService.getAllTasks()).thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .contains(responseDto);
    }

    @Test
    public void testSearchTask() {
        when(todoService.searchTasks("Test")).thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/todo/search")
                        .queryParam("name", "Test")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .contains(responseDto);
    }

    @Test
    public void testUpdateTask() {
        when(todoService.updateTask(any(String.class), any(TodoRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient.put()
                .uri("/todo/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test Task");
    }

    @Test
    public void testMarkCompleted() {
        responseDto.setCompleted(true);
        when(todoService.markCompleted("1")).thenReturn(Mono.just(responseDto));

        webTestClient.patch()
                .uri("/todo/1/complete")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseDto.class)
                .value(dto -> dto.isCompleted());
    }

    @Test
    public void testDeleteTask() {
        when(todoService.deleteTask("1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/todo/delete/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetCompletedTasks() {
        responseDto.setCompleted(true);

        when(todoService.getCompletedTasks()).thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo/completed")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .contains(responseDto);
    }

    @Test
    public void testGetPendingTasks() {
        responseDto.setCompleted(false);

        when(todoService.getPendingTasks()).thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo/pending")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .contains(responseDto);
    }
}
