package com.sony.todoapp.repository;

import com.sony.todoapp.entity.Todo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TodoRepository extends ReactiveMongoRepository<Todo, String> {
    Flux<Todo> findByNameContainingIgnoreCase(String name, String userId);

    Flux<Todo> findByCompleted(boolean completed);

    Flux<Todo> findByUserId(String userId);

    Mono<Todo> findByIdAndUserId(String id, String userId);

    Flux<Todo> findByCompletedAndUserId(boolean b, String userId);
}
