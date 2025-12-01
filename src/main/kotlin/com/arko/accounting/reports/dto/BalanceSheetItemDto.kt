package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class BalanceSheetItemDto(
    val accountName: String,
    val amount: BigDecimal
)