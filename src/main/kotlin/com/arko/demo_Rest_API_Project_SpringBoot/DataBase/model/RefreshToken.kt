package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "refresh_token")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant,
    val hashedToken: String,
    val createdAt: Instant = Instant.now(),
)
