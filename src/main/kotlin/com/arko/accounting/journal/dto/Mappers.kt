package com.arko.accounting.journal.dto

import com.arko.accounting.journal.domain.JournalEntry
import com.arko.accounting.journal.domain.JournalLine

fun JournalEntry.toDto(lines: List<JournalLine>): JournalEntryDto =
    JournalEntryDto(
        id = id,
        date = date,
        reference = reference,
        description = description,
        status = status,
        lines = lines.map {
            JournalLineDto(
                accountId = it.accountId,
                debit = it.debit,
                credit = it.credit,
                description = it.description
            )
        }
    )
