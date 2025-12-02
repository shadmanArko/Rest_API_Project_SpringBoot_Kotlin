package com.arko.accounting.ledger.repository

import com.arko.accounting.ledger.domain.LedgerEntry
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LedgerEntryRepository : JpaRepository<LedgerEntry, Long> {
    fun findByAccountId(accountId: UUID): List<LedgerEntry>
    fun findByJournalEntryId(journalEntryId: UUID): List<LedgerEntry>
}
