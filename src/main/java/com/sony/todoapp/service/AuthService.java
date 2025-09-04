package com.sony.todoapp.service;

import com.sony.todoapp.dto.UserRequestDto;
import com.sony.todoapp.dto.UserResponseDto;
import com.sony.todoapp.entity.User;
import com.sony.todoapp.exception.InvalidCredentialsException;
import com.sony.todoapp.exception.UserAlreadyExistsException;
import com.sony.todoapp.mapper.UserMapper;
import com.sony.todoapp.repository.UserRepository;
import com.sony.todoapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // Register a new user
    public Mono<UserResponseDto> register(UserRequestDto dto) {
        return userRepository.findByUsername(dto.getUsername())
                // If username exists, error
                .flatMap(existing -> Mono.<UserResponseDto>error(new UserAlreadyExistsException("Username already exists")))
                // If username does not exist, save new user
                .switchIfEmpty(
                        Mono.defer(() -> {
                            // Convert DTO -> Entity
                            User user = UserMapper.toEntity(dto);

                            // Encode password
                            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

                            // Save user and map to response DTO
                            return userRepository.save(user)
                                    .map(saved -> {
                                        String token = JwtUtil.generateToken(saved.getId());
                                        return UserMapper.toDto(saved, token);
                                    });
                        })
                );
    }


    // Login existing user
    public Mono<UserResponseDto> login(UserRequestDto dto) {
        return userRepository.findByUsername(dto.getUsername())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid username or password")))
                .flatMap(user -> {
                    if (bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
                        String token = JwtUtil.generateToken(user.getId());
                        return Mono.just(UserMapper.toDto(user, token));
                    } else {
                        return Mono.error(new InvalidCredentialsException("Invalid username or password"));
                    }
                });
    }


}
