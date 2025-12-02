package com.arko.accounting.journal.repository

import com.arko.accounting.journal.domain.JournalLine
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JournalLineRepository : JpaRepository<JournalLine, UUID> {

    fun findAllByJournalEntryId(journalEntryId: UUID): List<JournalLine>
}
