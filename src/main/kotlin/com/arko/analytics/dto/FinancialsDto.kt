package com.arko.analytics.dto

import java.math.BigDecimal
import java.time.LocalDate

data class FinancialsDto(
    val date: LocalDate,
    val revenue: BigDecimal,
    val spend: BigDecimal,
    val profit: BigDecimal,
    val roas: BigDecimal
)

data class FinancialsResponse(
    val summary: OverviewDto,
    val series: List<FinancialsDto>
)
