package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class IncomeStatementItemDto(
    val accountName: String,
    val amount: BigDecimal
)