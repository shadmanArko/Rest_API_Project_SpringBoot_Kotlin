package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class IncomeStatementSectionDto(
    val title: String,
    val items: List<IncomeStatementItemDto>,
    val total: BigDecimal
)

