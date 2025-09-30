package com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository

import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<CommentEntity, Long> {
}