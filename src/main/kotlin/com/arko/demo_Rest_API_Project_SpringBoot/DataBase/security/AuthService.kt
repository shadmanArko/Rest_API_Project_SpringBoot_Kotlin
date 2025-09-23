package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.security



import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.RefreshToken
import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.User
import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository.RefreshTokenRepository
import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository.UserRepository
import org.apache.el.parser.Token
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        return userRepository.save(
            User(
            email = email,
            hashedPassword = hashEncoder.encode(password)
        ))
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid Credentials")

        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid Credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun hashToToken(token: String) : String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String){
        val hashed = hashToToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plus(expiryMs, ChronoUnit.MILLIS)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    fun refresh(refreshToken: String): TokenPair {
        if (jwtService.validateRefreshToken(refreshToken)) {
            throw IllegalArgumentException("Invalid Refresh Token")
        }

        val userId =jwtService.getUserIdFromToken(refreshToken)

        val user = userRepository.findById(ObjectId(userId)).orElseThrow{ IllegalArgumentException("Invalid refresh token") }

        val hashed = hashToToken(refreshToken)

        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw IllegalArgumentException("Refresh token not recognized or already used by user")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

}