package com.arko.demo_Rest_API_Project_SpringBoot.Services

import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.repository.CommentRepository
import com.arko.demo_Rest_API_Project_SpringBoot.Dtos.CommentDto
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository) {

    fun insertComment(comment: CommentDto): CommentDto {
        return commentRepository
            .save(
                comment.toEntity().apply {
                    this.id = 0
                }
            )
            .toDto()
    }

    fun getComments(query: String?): List<CommentDto> {

        return commentRepository
            .findAll()
            .map { it.toDto() }
    }
}