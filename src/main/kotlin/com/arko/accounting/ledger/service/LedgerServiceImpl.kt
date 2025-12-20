package com.arko.accounting.ledger.service

import com.arko.accounting.account.domain.AccountCategory
import com.arko.accounting.account.domain.AccountType
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.analytics.events.AccountingEvent
import com.arko.accounting.analytics.events.CampaignEvent
import com.arko.accounting.analytics.publisher.AnalyticsEventPublisher
import com.arko.accounting.journal.domain.JournalEntry
import com.arko.accounting.journal.domain.JournalLine
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
     * SINGLE source of truth for:
     * - Ledger entries
     * - Ledger balances
     * - Analytics events
     */
    @Transactional
    override fun postJournalEntry(journalEntry: JournalEntry) {

        journalEntry.lines.forEach { line ->

            // 1️⃣ Persist ledger entry
            ledgerEntryRepo.save(
                LedgerEntry(
                    journalEntryId = journalEntry.id,
                    accountId = line.accountId,
                    debit = line.debit,
                    credit = line.credit
                )
            )

            // 2️⃣ Update running balance
            val existing = ledgerBalanceRepo.findById(line.accountId)
                .orElse(LedgerBalance(line.accountId))

            ledgerBalanceRepo.save(
                existing.copy(
                    debit = existing.debit + line.debit,
                    credit = existing.credit + line.credit
                )
            )

            // 3️⃣ Emit analytics events (line-level)
            emitAnalyticsEvents(journalEntry, line)
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

    private fun emitAnalyticsEvents(
        journalEntry: JournalEntry,
        line: JournalLine
    ) {
        val account = accountRepo.findById(line.accountId)
            .orElseThrow { IllegalStateException("Account not found: ${line.accountId}") }

        // ✅ Normalize amount for analytics
        val amount = when (account.type) {
            AccountType.EXPENSE -> line.debit
            AccountType.REVENUE -> line.credit
            else -> BigDecimal.ZERO
        }

        if (amount <= BigDecimal.ZERO) return

        // 📊 Accounting analytics event
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

        // 📣 Campaign analytics (OPTIONAL, line-level)
        if (
            account.type == AccountType.EXPENSE &&
            account.category == AccountCategory.MARKETING &&
            line.campaignId != null
        ) {
            analyticsPublisher.publishCampaignEvent(
                CampaignEvent(
                    eventDate = journalEntry.date,
                    companyId = journalEntry.companyId,
                    campaignId = line.campaignId,
                    eventType = "COST",
                    amount = amount
                )
            )
        }
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

    private fun LedgerBalance.toBalanceResponse(): LedgerBalanceResponse =
        LedgerBalanceResponse(
            accountId = accountId,
            debit = debit,
            credit = credit,
            netBalance = debit.subtract(credit)
        )
}
