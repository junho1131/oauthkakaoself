package com.example.kakaoauth.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(private val jwtSigningKey: java.security.Key) {

    companion object {
        private const val ACCESS_TOKEN_EXPIRATION = 3600000L // 1시간
        private const val REFRESH_TOKEN_EXPIRATION = 604800000L // 7일
    }

    fun generateTokens(authentication: Authentication): Map<String, String> {
        val user = authentication.principal as OAuth2User
        val now = Date()
        
        val accessToken = generateAccessToken(user, now)
        val refreshToken = generateRefreshToken(user, now)
        
        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    private fun generateAccessToken(user: OAuth2User, now: Date): String {
        val expiryDate = Date(now.time + ACCESS_TOKEN_EXPIRATION)
        
        return Jwts.builder()
            .setSubject(user.name)
            .claim("name", user.attributes["name"])
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
            .compact()
    }

    private fun generateRefreshToken(user: OAuth2User, now: Date): String {
        val expiryDate = Date(now.time + REFRESH_TOKEN_EXPIRATION)
        
        return Jwts.builder()
            .setSubject(user.name)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(jwtSigningKey)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }

    fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtSigningKey)
                .build()
                .parseClaimsJws(refreshToken)
                .body

            val now = Date()
            val expiryDate = Date(now.time + ACCESS_TOKEN_EXPIRATION)

            Jwts.builder()
                .setSubject(claims.subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
                .compact()
        } catch (e: Exception) {
            null
        }
    }
} 