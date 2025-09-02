package com.sony.todoapp.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
public class SecurityConfigTest {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll() // allow all requests
                );
        return http.build();
    }
}

