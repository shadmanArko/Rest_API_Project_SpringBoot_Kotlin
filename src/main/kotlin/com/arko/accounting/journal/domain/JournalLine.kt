package com.arko.accounting.journal.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "journal_lines")
class JournalLine(

    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    val journalEntry: JournalEntry = JournalEntry(),

    @Column(nullable = false)
    val accountId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val debit: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val credit: BigDecimal = BigDecimal.ZERO,

    val description: String? = null
)
