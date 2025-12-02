package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class CashFlowSectionDto(
    val title: String,
    val amount: BigDecimal
)

