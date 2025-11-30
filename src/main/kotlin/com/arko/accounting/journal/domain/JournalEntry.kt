package com.arko.accounting.journal.domain

import com.arko.accounting.journal.domain.JournalEntryStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "journal_entries")
class JournalEntry(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val companyId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),

    var reference: String? = null,

    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: JournalEntryStatus = JournalEntryStatus.DRAFT,

    @CreationTimestamp
    val createdAt: Instant? = null
)
