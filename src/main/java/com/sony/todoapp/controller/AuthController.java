package com.sony.todoapp.controller;

import com.sony.todoapp.dto.UserRequestDto;
import com.sony.todoapp.dto.UserResponseDto;
import com.sony.todoapp.exception.InvalidCredentialsException;
import com.sony.todoapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Register a new user
    @PostMapping("/register")
    public Mono<ResponseEntity<UserResponseDto>> register(@RequestBody UserRequestDto requestDto) {
        return authService.register(requestDto)
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null)));
    }

    // Login an existing user
    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody UserRequestDto requestDto) {
        return authService.login(requestDto)
                .map(userResp -> ResponseEntity.<Object>ok(userResp)) // Explicitly use Object
                .onErrorResume(InvalidCredentialsException.class, e ->
                        Mono.just(ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", e.getMessage())))
                );
    }

}
