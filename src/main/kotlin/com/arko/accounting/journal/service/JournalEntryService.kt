package com.arko.accounting.journal.service

import com.arko.accounting.journal.dto.CreateJournalEntryRequest
import com.arko.accounting.journal.dto.JournalEntryDto
import java.util.*

interface JournalEntryService {
    fun create(companyId: UUID, req: CreateJournalEntryRequest): JournalEntryDto
    fun get(id: UUID): JournalEntryDto
}
