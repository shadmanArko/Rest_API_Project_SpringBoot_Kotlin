package com.arko.demo_Rest_API_Project_SpringBoot.Services

import com.arko.demo_Rest_API_Project_SpringBoot.DataBase.model.CommentEntity
import com.arko.demo_Rest_API_Project_SpringBoot.Dtos.CommentDto

fun CommentEntity.toDto(): CommentDto{
    return CommentDto(
        id = this.id,
        content = this.content,
        author = this.author,
    )
}

fun CommentDto.toEntity(): CommentEntity {
    return CommentEntity(
        id = this.id,
        content = this.content,
        author = this.author,
    )
}