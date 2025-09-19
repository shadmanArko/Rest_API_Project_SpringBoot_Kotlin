package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("JWT_SECRET_BASE64") private val jwtSecret: String
) {
    private val secretkey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))

    private val accessTokenValidityMs = 15L * 60L * 1000L
    private val refreshTokenValidityMs = 30L * 24L * 60L * 60L * 1000L

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long): String {

        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretkey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, "refresh", refreshTokenValidityMs)
    }

    private fun parseAllClaimsFromToken(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        }
        else token

        return try {
            Jwts.parser()
                .verifyWith(secretkey)
                .build()
                .parseSignedClaims(token)
                .payload
        }
        catch (e: Exception){
            null
        }
    }

    fun validateAccessToken(token: String): Boolean{
        val claims = parseAllClaimsFromToken(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean{
        val claims = parseAllClaimsFromToken(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    fun getUserIdFromToken(token: String): String?{
        val claims = parseAllClaimsFromToken(token) ?:
        throw IllegalArgumentException("Invalid token")
        return claims.subject
    }
}