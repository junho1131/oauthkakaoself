package com.example.kakaoauth.config

import com.example.kakaoauth.service.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2SuccessHandler(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val tokens = jwtService.generateTokens(authentication)
        val user = authentication.principal as OAuth2User
        
        // Access Token 쿠키 설정
        val accessTokenCookie = Cookie("accessToken", tokens["accessToken"]).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 3600 // 1시간
        }
        
        // Refresh Token 쿠키 설정
        val refreshTokenCookie = Cookie("refreshToken", tokens["refreshToken"]).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 604800 // 7일
        }
        
        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)
        
        // 사용자 정보만 URL 파라미터로 전달
        val userInfo = mapOf(
            "user" to mapOf(
                "id" to user.name,
                "name" to user.attributes["name"]
            )
        )
        
        val jsonResponse = objectMapper.writeValueAsString(userInfo)
        val encodedResponse = URLEncoder.encode(jsonResponse, StandardCharsets.UTF_8)
        
        // 프론트엔드로 리다이렉트
        response.sendRedirect("http://localhost:3000?data=$encodedResponse")
    }
} 