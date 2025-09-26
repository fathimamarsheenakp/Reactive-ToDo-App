package com.sony.todoapp.controller;

import com.sony.todoapp.dto.TodoRequestDto;
import com.sony.todoapp.dto.TodoResponseDto;
import com.sony.todoapp.entity.User;
import com.sony.todoapp.repository.UserRepository;
import com.sony.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebFluxTest(TodoController.class)
@Import(TodoControllerTest.TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TodoControllerTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TodoService todoService;

    private TodoRequestDto requestDto;
    private TodoResponseDto responseDto;
    private User mockUser;

    @BeforeEach
    void setUp() {

        requestDto = new TodoRequestDto();
        requestDto.setName("Task 1");
        requestDto.setDescription("Desc 1");

        responseDto = new TodoResponseDto();
        responseDto.setId("1");
        responseDto.setName("Task 1");
        responseDto.setDescription("Desc 1");
        responseDto.setCompleted(false);
        responseDto.setUserId("user123");

        User mockUser = new User();
        mockUser.setId("user123");

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(mockUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    static class TestSecurityConfig {
        @org.springframework.context.annotation.Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                            .anyExchange().permitAll()
                    )
                    .build();
        }
    }

    @Test
    @WithMockUser(username = "user123") // mock authenticator user
    void testAddTask() {
        Mockito.when(todoService.addTask(Mockito.any(TodoRequestDto.class), Mockito.eq("user123")))
                .thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/todo/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseDto.class)
                .value(resp -> {
                    assert resp.getId().equals("1");
                    assert resp.getName().equals("Task 1");
                    assert resp.getUserId().equals("user123");
                    assert !resp.isCompleted();
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testGetAll() {
        Mockito.when(todoService.getAllTasks(Mockito.eq("user123")))
                .thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .value(tasks -> {
                    assertEquals(1, tasks.size());
                    TodoResponseDto resp = tasks.get(0);
                    assertEquals("1", resp.getId());
                    assertEquals("Task 1", resp.getName());
                    assertEquals("user123", resp.getUserId());
                    assertFalse(resp.isCompleted());
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testSearch() {
        Mockito.when(todoService.searchTasks(Mockito.eq("Task 1"), Mockito.eq("user123")))
                .thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/todo/search")
                        .queryParam("keyword", "Task 1")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TodoResponseDto.class)
                .value(tasks -> {
                    assertEquals(1, tasks.size());
                    TodoResponseDto resp = tasks.get(0);
                    assertEquals("1", resp.getId());
                    assertEquals("Task 1", resp.getName());
                    assertEquals("user123", resp.getUserId());
                    assertFalse(resp.isCompleted());
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testUpdateTask() {

        TodoRequestDto updateDto = new TodoRequestDto();
        updateDto.setName("Updated Task");
        updateDto.setDescription("Updated Description");

        TodoResponseDto updatedResponse = new TodoResponseDto();
        updatedResponse.setId("1");
        updatedResponse.setName("Updated Task");
        updatedResponse.setDescription("Updated Description");
        updatedResponse.setUserId("user123");
        updatedResponse.setCompleted(false);

        Mockito.when(todoService.updateTask(Mockito.eq("1"), Mockito.any(TodoRequestDto.class), Mockito.eq("user123")))
                .thenReturn(Mono.just(updatedResponse));

        webTestClient.put()
                .uri("/todo/edit/{id}", "1")
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoResponseDto.class)
                .value(resp -> {
                    assertEquals("1", resp.getId());
                    assertEquals("Updated Task", resp.getName());
                    assertEquals("Updated Description", resp.getDescription());
                    assertEquals("user123", resp.getUserId());
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testMarkComplete() {
        responseDto.setCompleted(true);

        Mockito.when(todoService.markCompleted(Mockito.eq("1"), Mockito.eq("user123")))
                .thenReturn(Mono.just(responseDto));

        webTestClient.patch()
                .uri("/todo/{id}/complete", "1")
                .exchange()
                .expectBody(TodoResponseDto.class)
                .isEqualTo(responseDto)
                .value(resp -> {
                    assert resp.getId().equals("1");
                    assert resp.getName().equals("Task 1");
                    assert resp.getUserId().equals("user123");
                    assert resp.isCompleted();
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testDeleteTask() {
        Mockito.when(todoService.deleteTask(Mockito.eq("1"), Mockito.eq("user123")))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/todo/delete/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser(username = "user123")
    void testGetCompletedTasks() {
        Mockito.when(todoService.getCompletedTasksEs(Mockito.eq("user123")))
                .thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo/completed")
                .exchange()
                .expectBodyList(TodoResponseDto.class)
                .value(tasks -> {
                    assertEquals(1, tasks.size());
                    TodoResponseDto resp = tasks.get(0);
                    assertEquals("1", resp.getId());
                    assertEquals("Task 1", resp.getName());
                    assertEquals("user123", resp.getUserId());
                    assertFalse(resp.isCompleted());
                });
    }

    @Test
    @WithMockUser(username = "user123")
    void testGetPendingTasks() {
        Mockito.when(todoService.getPendingTasksEs(Mockito.eq("user123")))
                .thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/todo/pending")
                .exchange()
                .expectBodyList(TodoResponseDto.class)
                .value(tasks -> {
                    assertEquals(1, tasks.size());
                    TodoResponseDto resp = tasks.get(0);
                    assertEquals("1", resp.getId());
                    assertEquals("Task 1", resp.getName());
                    assertEquals("user123", resp.getUserId());
                    assert (!resp.isCompleted());
                });
    }
}
