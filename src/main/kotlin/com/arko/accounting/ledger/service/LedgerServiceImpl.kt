package com.arko.accounting.ledger.service

import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.accounting.analytics.publisher.AnalyticsEventPublisher
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
    private val ledgerEntryRepo: LedgerEntryRepository,
    private val ledgerBalanceRepo: LedgerBalanceRepository,
    private val accountRepo: AccountRepository,
    private val analyticsPublisher: AnalyticsEventPublisher
) : LedgerService {

    /**
     * Posts a journal entry into the ledger.
     * This is the SINGLE source of truth for:
     * - Ledger entries
     * - Ledger balances
     * - Analytics events
     */
    @Transactional
    override fun postJournalEntry(journalEntry: JournalEntry) {

        journalEntry.lines.forEach { line ->

            // 1️⃣ Persist ledger entry
            val ledgerEntry = LedgerEntry(
                journalEntryId = journalEntry.id,
                accountId = line.accountId,
                debit = line.debit,
                credit = line.credit
            )
            ledgerEntryRepo.save(ledgerEntry)

            // 2️⃣ Update running balance (idempotent-safe)
            val balance = ledgerBalanceRepo.findById(line.accountId)
                .orElse(LedgerBalance(line.accountId))

            val updatedBalance = balance.copy(
                debit = balance.debit + line.debit,
                credit = balance.credit + line.credit
            )
            ledgerBalanceRepo.save(updatedBalance)

            // 3️⃣ Emit analytics event
            emitAnalyticsEvent(journalEntry, line.accountId, line.debit, line.credit)
        }
    }

    // -------------------- READ APIs --------------------

    override fun getLedgerEntries(): List<LedgerEntryResponse> =
        ledgerEntryRepo.findAll().map { it.toResponse() }

    override fun getLedgerForAccount(accountId: UUID): List<LedgerEntryResponse> =
        ledgerEntryRepo.findByAccountId(accountId).map { it.toResponse() }

    override fun getBalances(): List<LedgerBalanceResponse> =
        ledgerBalanceRepo.findAll().map { it.toBalanceResponse() }

    // -------------------- ANALYTICS --------------------

    private fun emitAnalyticsEvent(
        journalEntry: JournalEntry,
        accountId: UUID,
        debit: BigDecimal,
        credit: BigDecimal
    ) {
        val account = accountRepo.findById(accountId)
            .orElseThrow { IllegalStateException("Account not found: $accountId") }

        val amount = debit.subtract(credit)

        analyticsPublisher.publish(
            AccountingEvent(
                eventDate = journalEntry.date,
                companyId = journalEntry.companyId,
                accountCode = account.code,
                accountType = account.type.name,
                amount = amount,
                source = "JOURNAL"
            )
        )
    }

    // -------------------- MAPPERS --------------------

    private fun LedgerEntry.toResponse() =
        LedgerEntryResponse(
            id = id,
            journalEntryId = journalEntryId,
            accountId = accountId,
            debit = debit,
            credit = credit
        )

    private fun LedgerBalance.toBalanceResponse(): LedgerBalanceResponse {
        val net = debit.subtract(credit)
        return LedgerBalanceResponse(
            accountId = accountId,
            debit = debit,
            credit = credit,
            netBalance = net
        )
    }
}
