package com.sony.todoapp.repository;

import com.sony.todoapp.entity.Todo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;


public interface TodoRepository extends ReactiveMongoRepository<Todo, String> {
    Flux<Todo> findByNameContainingIgnoreCase(String name);

    Flux<Todo> findByCompleted(boolean completed);

}
