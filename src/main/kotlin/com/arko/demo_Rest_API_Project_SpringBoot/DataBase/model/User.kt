package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document("User")
data class User(
    val email: String,
    val hashedPassword: String,
    val id: ObjectId,
)
