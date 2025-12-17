package com.arko.accounting.analytics.events

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class AccountingEvent(
    val eventDate: LocalDate,
    val companyId: UUID,
    val accountCode: String,
    val accountType: String, // EXPENSE / INCOME
    val amount: BigDecimal,
    val source: String // JOURNAL
)
