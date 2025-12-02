package com.arko.accounting.account.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "accounts")
data class Account(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val code: String = "",

    @Column(nullable = false)
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: AccountType = AccountType.ASSET,

    @Column(nullable = false)
    val isParent: Boolean = false,

    @Column(nullable = true)
    val parentId: UUID? = null,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = true)
    val description: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
