package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository

import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId> {
    fun findByEmail(email: String): User?
}