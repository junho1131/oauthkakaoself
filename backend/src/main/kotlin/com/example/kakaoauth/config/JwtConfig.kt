package com.example.kakaoauth.config

import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.Key

@Configuration
class JwtConfig {
    @Bean
    fun jwtSigningKey(): Key {
        val secret = System.getenv("JWT_SECRET") ?: "your-secret-key-must-be-at-least-256-bits-long"
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }
} 