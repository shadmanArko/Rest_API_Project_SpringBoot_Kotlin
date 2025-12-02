package com.arko.accounting.reports.service

import com.arko.accounting.reports.dto.*
import java.util.UUID

interface FinancialReportService {

    fun generateBalanceSheet(companyId: UUID): BalanceSheetDto

    fun generateIncomeStatement(companyId: UUID): IncomeStatementDto

    fun generateCashFlow(companyId: UUID): CashFlowStatementDto
}
