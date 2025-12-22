package com.arko.analytics.dto.dashboard

data class FinancialTrendDto(
    val dailyRevenue: List<AmountByDateDto>,
    val dailyExpense: List<AmountByDateDto>
)