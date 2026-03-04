package com.arko.accounting.journal.service

import com.arko.accounting.journal.domain.*
import com.arko.accounting.journal.dto.*
import com.arko.accounting.journal.exception.UnbalancedJournalException
import com.arko.accounting.journal.repository.JournalEntryRepository
import com.arko.accounting.journal.repository.JournalLineRepository
import com.arko.accounting.ledger.service.LedgerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import com.arko.accounting.client.AnalyticsClient
import com.arko.accounting.client.AnalyticsEventDto
import org.slf4j.LoggerFactory
import java.math.BigDecimal

@Service
class JournalEntryServiceImpl(
    private val entryRepo: JournalEntryRepository,
    private val lineRepo: JournalLineRepository,
    private val ledgerService: LedgerService,
    private val analyticsClient: AnalyticsClient
) : JournalEntryService {

    private val logger = LoggerFactory.getLogger(JournalEntryServiceImpl::class.java)

    @Transactional
    override fun create(companyId: UUID, req: CreateJournalEntryRequest): JournalEntryDto {

        validateBalanced(req.lines)

        val entry = JournalEntry(
            companyId = companyId,
            date = req.date,
            reference = req.reference,
            description = req.description
        )
        entryRepo.save(entry)

        val lines = req.lines.map { dto ->
            JournalLine(
                journalEntry = entry,
                accountId = dto.accountId,
                debit = dto.debit,
                credit = dto.credit,
                description = dto.description
            )
        }

        lineRepo.saveAll(lines)

        // Add lines to the entry's collection for ledger posting
        entry.lines.addAll(lines)

        // POST TO LEDGER
        ledgerService.postJournalEntry(entry)

        // SEND ANALYTICS EVENT (Best Effort)
        try {
            val totalDebit = lines.sumOf { it.debit }
            analyticsClient.sendEvent(
                AnalyticsEventDto(
                    platform = "accounting_service",
                    eventType = "journal_entry_created",
                    amount = totalDebit,
                    currency = "USD" // Defaulting to USD for now
                )
            )
        } catch (e: Exception) {
            logger.error("Failed to send analytics event", e)
            // Do not rethrow - analytics failure should not rollback transaction
        }

        return entry.toDto(lines)
    }

    override fun get(id: UUID): JournalEntryDto {
        val entry = entryRepo.findById(id).orElseThrow { RuntimeException("Not found") }
        val lines = lineRepo.findAllByJournalEntryId(id)
        return entry.toDto(lines)
    }

    override fun getAll(companyId: UUID): List<JournalEntryDto> {
        return entryRepo.findAllByCompanyIdOrderByDateDesc(companyId).map { entry ->
            val lines = lineRepo.findAllByJournalEntryId(entry.id)
            entry.toDto(lines)
        }
    }

    @Transactional
    override fun update(companyId: UUID, id: UUID, req: CreateJournalEntryRequest): JournalEntryDto {
        val entry = entryRepo.findById(id).orElseThrow { RuntimeException("Journal Entry not found") }
        if (entry.companyId != companyId) throw RuntimeException("Unauthorized")

        validateBalanced(req.lines)

        // 1. REVERSE PREVIOUS LEDGER POSTINGS
        val oldLines = lineRepo.findAllByJournalEntryId(id)
        entry.lines.clear()
        entry.lines.addAll(oldLines) // Ensure lines are attached for reversal
        ledgerService.reverseJournalEntry(entry)

        // 2. UPDATE HEADER
        entry.date = req.date
        entry.reference = req.reference
        entry.description = req.description
        
        // 3. REPLACE LINES
        lineRepo.deleteAll(oldLines)
        val newLines = req.lines.map { dto ->
            JournalLine(
                journalEntry = entry,
                accountId = dto.accountId,
                debit = dto.debit,
                credit = dto.credit,
                description = dto.description
            )
        }
        lineRepo.saveAll(newLines)
        entry.lines.clear()
        entry.lines.addAll(newLines)

        // 4. POST TO LEDGER
        entryRepo.save(entry)
        ledgerService.postJournalEntry(entry)

        return entry.toDto(newLines)
    }

    private fun validateBalanced(lines: List<JournalLineDto>) {
        val totalDebit = lines.sumOf { it.debit }
        val totalCredit = lines.sumOf { it.credit }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw UnbalancedJournalException()
        }
    }
}
