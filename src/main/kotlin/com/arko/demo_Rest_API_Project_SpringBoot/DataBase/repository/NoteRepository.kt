package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository

import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}

