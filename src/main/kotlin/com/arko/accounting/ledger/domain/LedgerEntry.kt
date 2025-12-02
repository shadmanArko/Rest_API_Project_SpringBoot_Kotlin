package com.arko.accounting.ledger.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "ledger_entries")
data class LedgerEntry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val journalEntryId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val accountId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val debit: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val credit: BigDecimal = BigDecimal.ZERO,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
