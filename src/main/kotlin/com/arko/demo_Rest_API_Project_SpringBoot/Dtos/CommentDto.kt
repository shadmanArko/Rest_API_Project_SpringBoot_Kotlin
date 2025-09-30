package com.arko.demo_Rest_API_Project_SpringBoot.Dtos

import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class CommentDto (
    val id: Long,
    @field:Length(
        min = 5,
        max = 500,
        message = "Content must be between 5 and 500 characters"
    )
    val content: String,
    @field:Pattern(
        regexp = "\\b[a-zA-Z]+\\b(?:\\s+\\b[a-zA-Z]+\\b)+",
        message = "Author must at least consist of a first and last name"
    )
    val author: String,
)
