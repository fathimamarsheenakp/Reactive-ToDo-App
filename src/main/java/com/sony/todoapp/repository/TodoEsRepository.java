package com.sony.todoapp.repository;

import com.sony.todoapp.entity.TodoEs;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

public interface TodoEsRepository extends ReactiveElasticsearchRepository<TodoEs, String> {
    Flux<TodoEs> findByCompletedAndUserId(boolean completed, String userId);
    Flux<TodoEs> findByUserId(String userId);
    Flux<TodoEs> findByNameContainingIgnoreCaseAndUserId(String name, String userId);

    Flux<TodoEs> searchByUserId(String userId);

    Flux<TodoEs> searchByNameContainingIgnoreCaseAndUserId(String name, String userId);
}
