package com.arko.accounting.journal.dto

import com.arko.accounting.journal.domain.JournalEntryStatus
import java.time.LocalDate
import java.util.*

data class JournalEntryDto(
    val id: UUID,
    val date: LocalDate,
    val reference: String?,
    val description: String?,
    val status: JournalEntryStatus,
    val lines: List<JournalLineDto>
)
