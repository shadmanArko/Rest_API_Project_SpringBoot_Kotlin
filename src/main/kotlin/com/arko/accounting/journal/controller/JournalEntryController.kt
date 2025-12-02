package com.arko.accounting.journal.controller

import com.arko.accounting.journal.dto.CreateJournalEntryRequest
import com.arko.accounting.journal.dto.JournalEntryDto
import com.arko.accounting.journal.service.JournalEntryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/journal-entries")
class JournalEntryController(
    private val service: JournalEntryService
) {

    @PostMapping
    fun create(
        @RequestBody @Valid req: CreateJournalEntryRequest,
        @RequestHeader("X-Company-ID") companyId: UUID
    ): ResponseEntity<JournalEntryDto> {
        val dto = service.create(companyId, req)
        return ResponseEntity.ok(dto)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<JournalEntryDto> {
        return ResponseEntity.ok(service.get(id))
    }
}
