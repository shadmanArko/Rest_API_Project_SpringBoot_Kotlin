package com.arko.accounting.journal.service

import com.arko.accounting.journal.domain.*
import com.arko.accounting.journal.dto.*
import com.arko.accounting.journal.repository.JournalEntryRepository
import com.arko.accounting.journal.repository.JournalLineRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class JournalEntryService(
    private val entryRepo: JournalEntryRepository,
    private val lineRepo: JournalLineRepository
) {

    @Transactional
    fun create(companyId: UUID, req: CreateJournalEntryRequest): JournalEntryDto {

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

        return entry.toDto(lines)
    }

    fun get(id: UUID): JournalEntryDto {
        val entry = entryRepo.findById(id).orElseThrow { RuntimeException("Not found") }
        val lines = lineRepo.findAllByJournalEntryId(id)
        return entry.toDto(lines)
    }

    private fun validateBalanced(lines: List<JournalLineDto>) {
        val totalDebit = lines.sumOf { it.debit }
        val totalCredit = lines.sumOf { it.credit }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw UnbalancedJournalException()
        }
    }
}
