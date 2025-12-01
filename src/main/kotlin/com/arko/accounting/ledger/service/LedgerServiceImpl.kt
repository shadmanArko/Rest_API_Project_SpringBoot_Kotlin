package com.arko.accounting.ledger.service

import com.arko.accounting.journal.domain.JournalEntry
import com.arko.accounting.ledger.domain.LedgerBalance
import com.arko.accounting.ledger.domain.LedgerEntry
import com.arko.accounting.ledger.dto.LedgerBalanceResponse
import com.arko.accounting.ledger.dto.LedgerEntryResponse
import com.arko.accounting.ledger.repository.LedgerBalanceRepository
import com.arko.accounting.ledger.repository.LedgerEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class LedgerServiceImpl(
    private val entryRepo: LedgerEntryRepository,
    private val balanceRepo: LedgerBalanceRepository
) : LedgerService {

    @Transactional
    override fun postJournalEntry(journalEntry: JournalEntry) {

        journalEntry
        journalEntry.lines.forEach { line ->

            val entry = LedgerEntry(
                journalEntryId = journalEntry.id,
                accountId = line.accountId,
                debit = line.debit,
                credit = line.credit
            )

            entryRepo.save(entry)

            // Update running balance
            val existing = balanceRepo.findById(line.accountId).orElse(
                LedgerBalance(line.accountId)
            )

            val updated = existing.copy(
                debit = existing.debit + line.debit,
                credit = existing.credit + line.credit
            )

            balanceRepo.save(updated)
        }
    }

    override fun getLedgerEntries(): List<LedgerEntryResponse> =
        entryRepo.findAll().map { it.toResponse() }

    override fun getLedgerForAccount(accountId: UUID): List<LedgerEntryResponse> =
        entryRepo.findByAccountId(accountId).map { it.toResponse() }

    override fun getBalances(): List<LedgerBalanceResponse> =
        balanceRepo.findAll().map { it.toBalanceResponse() }

    private fun LedgerEntry.toResponse() =
        LedgerEntryResponse(
            id = id,
            journalEntryId = journalEntryId,
            accountId = accountId,
            debit = debit,
            credit = credit
        )

    private fun LedgerBalance.toBalanceResponse(): LedgerBalanceResponse {
        val net = debit - credit
        return LedgerBalanceResponse(
            accountId = accountId,
            debit = debit,
            credit = credit,
            netBalance = net
        )
    }
}
