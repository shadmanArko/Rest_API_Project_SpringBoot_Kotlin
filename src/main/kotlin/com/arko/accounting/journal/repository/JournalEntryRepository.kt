package com.arko.accounting.journal.repository

import com.arko.accounting.journal.domain.JournalEntry
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JournalEntryRepository : JpaRepository<JournalEntry, UUID>
