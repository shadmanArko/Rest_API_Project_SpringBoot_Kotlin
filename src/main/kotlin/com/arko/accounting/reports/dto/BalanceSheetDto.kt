package com.arko.accounting.reports.dto

data class BalanceSheetDto(
    val assets: BalanceSheetSectionDto,
    val liabilities: BalanceSheetSectionDto,
    val equity: BalanceSheetSectionDto,
    val isBalanced: Boolean
)