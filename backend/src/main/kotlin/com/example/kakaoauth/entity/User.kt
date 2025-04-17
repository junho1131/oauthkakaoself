package com.example.kakaoauth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val kakaoId: String,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val profileImageUrl: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val lastLoginAt: LocalDateTime = LocalDateTime.now()
) 