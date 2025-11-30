package com.arko.accounting.journal.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateJournalEntryRequest(

    @field:NotNull
    val date: LocalDate,

    val reference: String?,
    val description: String?,

    @field:Valid
    @field:NotEmpty
    val lines: List<JournalLineDto>
)
