package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class BalanceSheetSectionDto(
    val title: String,
    val items: List<BalanceSheetItemDto>,
    val total: BigDecimal
)

