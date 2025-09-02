package com.sony.todoapp.controller;

import com.sony.todoapp.dto.UserRequestDto;
import com.sony.todoapp.dto.UserResponseDto;
import com.sony.todoapp.repository.UserRepository;
import com.sony.todoapp.security.SecurityConfigTest;
import com.sony.todoapp.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthController.class)
@Import(SecurityConfigTest.class)
public class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    private UserRequestDto requestDto;
    private UserResponseDto responseDto;

    @BeforeEach
    public void setUp() {
        requestDto = new UserRequestDto(
                "manupradeep",
                "manu@123"
        );

        responseDto = new UserResponseDto();
        responseDto.setUsername("manupradeep");
        responseDto.setToken("dummy-jwt-token");
    }

    @Test
    void register_Success() {
        when(authService.register(any(UserRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("manupradeep")
                .jsonPath("$.token").isEqualTo("dummy-jwt-token");
    }
}
