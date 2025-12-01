package com.arko.accounting.ledger.service

import com.arko.accounting.journal.domain.JournalEntry
import com.arko.accounting.ledger.dto.LedgerBalanceResponse
import com.arko.accounting.ledger.dto.LedgerEntryResponse
import java.util.UUID

interface LedgerService {
    fun postJournalEntry(journalEntry: JournalEntry)
    fun getLedgerEntries(): List<LedgerEntryResponse>
    fun getLedgerForAccount(accountId: UUID): List<LedgerEntryResponse>
    fun getBalances(): List<LedgerBalanceResponse>
}
