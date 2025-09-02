package com.sony.todoapp.repository;


import com.sony.todoapp.dto.UserResponseDto;
import com.sony.todoapp.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
