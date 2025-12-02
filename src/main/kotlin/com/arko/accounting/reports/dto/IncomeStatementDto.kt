package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class IncomeStatementDto(
    val revenue: IncomeStatementSectionDto,
    val expenses: IncomeStatementSectionDto,
    val netIncome: BigDecimal
)