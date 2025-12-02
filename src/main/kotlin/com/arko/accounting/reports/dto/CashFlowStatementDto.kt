package com.arko.accounting.reports.dto

import java.math.BigDecimal

data class CashFlowStatementDto(
    val operating: CashFlowSectionDto,
    val investing: CashFlowSectionDto,
    val financing: CashFlowSectionDto,
    val netCashFlow: BigDecimal
)