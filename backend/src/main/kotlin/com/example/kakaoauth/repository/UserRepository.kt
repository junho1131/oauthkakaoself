package com.example.kakaoauth.repository

import com.example.kakaoauth.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByKakaoId(kakaoId: String): User?
} 