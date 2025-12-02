package com.arko.accounting.journal.dto

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

data class JournalLineDto(
    @field:NotNull
    val accountId: UUID,

    val debit: BigDecimal = BigDecimal.ZERO,
    val credit: BigDecimal = BigDecimal.ZERO,

    val description: String? = null
)
