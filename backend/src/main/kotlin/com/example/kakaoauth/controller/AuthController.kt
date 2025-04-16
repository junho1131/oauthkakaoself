package com.example.kakaoauth.controller

import com.example.kakaoauth.service.JwtService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")
class AuthController(
    private val jwtService: JwtService
) {

    @PostMapping("/refresh")
    fun refreshToken(@CookieValue("refreshToken") refreshToken: String): ResponseEntity<Void> {
        val newAccessToken = jwtService.refreshAccessToken(refreshToken)
            ?: return ResponseEntity.badRequest().build()

        val accessTokenCookie = Cookie("accessToken", newAccessToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 3600 // 1시간
        }

        return ResponseEntity.ok()
            .header("Set-Cookie", accessTokenCookie.toString())
            .build()
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        // Access Token 쿠키 삭제
        val accessTokenCookie = Cookie("accessToken", "").apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 0
        }
        
        // Refresh Token 쿠키 삭제
        val refreshTokenCookie = Cookie("refreshToken", "").apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 0
        }
        
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)
        
        return ResponseEntity.ok().build()
    }
} 