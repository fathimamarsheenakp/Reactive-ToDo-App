package com.sony.todoapp.service;

import com.sony.todoapp.dto.UserRequestDto;
import com.sony.todoapp.entity.User;
import com.sony.todoapp.exception.UserAlreadyExistsException;
import com.sony.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private UserRequestDto requestDto;
    private User savedUser;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new UserRequestDto(
                "manupradeep",
                "manu@123"
        );

        savedUser = new User();
        savedUser.setId("1");
        savedUser.setUsername(requestDto.getUsername());
        savedUser.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
    }

    @Test
    public void testRegister_Success() {
        when(userRepository.findByUsername(requestDto.getUsername()))
                .thenReturn(Mono.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(authService.register(requestDto))
                .expectNextMatches(response ->
                        response.getUsername().equals(requestDto.getUsername()) &&
                                response.getToken() != null && !response.getToken().isEmpty()
                )
                .verifyComplete();
    }

    @Test
    public void testRegister_Failure() {
        when(userRepository.findByUsername(requestDto.getUsername()))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(authService.register(requestDto))
                .expectErrorMatches(e ->
                        e instanceof UserAlreadyExistsException &&
                                e.getMessage().equals("Username already exists")
                )
                .verify();
    }

    @Test
    public void testLogin_Success() {
        when(userRepository.findByUsername(requestDto.getUsername()))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(authService.login(requestDto))
                .expectNextMatches(response ->
                        response.getUsername().equals(requestDto.getUsername()) &&
                                response.getToken() != null && !response.getToken().isEmpty()
                )
                .verifyComplete();

    }

    @Test
    public void login_Failure_WrongPassword() {
        requestDto.setPassword("wrongPassword");

        when(userRepository.findByUsername(requestDto.getUsername()))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(authService.login(requestDto))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().equals("Invalid username or password")
                )
                .verify();
    }

    @Test
    void login_Failure_UsernameNotFound() {
        when(userRepository.findByUsername(requestDto.getUsername()))
                .thenReturn(Mono.empty()); // username does not exist

        StepVerifier.create(authService.login(requestDto))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().equals("Invalid username or password")
                )
                .verify();

        verify(userRepository).findByUsername(requestDto.getUsername());
        verifyNoMoreInteractions(userRepository);
    }
}
