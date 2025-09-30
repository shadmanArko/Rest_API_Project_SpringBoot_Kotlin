package com.arko.demo_Rest_API_Project_SpringBoot.controllers

import com.arko.demo_Rest_API_Project_SpringBoot.Dtos.CommentDto
import com.arko.demo_Rest_API_Project_SpringBoot.Services.CommentService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping
    fun getComments(
        @RequestParam("q", required = false) query: String?
    ): List<CommentDto> {
        return commentService.getComments(query)
    }

    @PostMapping
    fun postComment(
        @Valid @RequestBody dto: CommentDto
    ): CommentDto {
        return commentService.insertComment(dto)
    }
}
